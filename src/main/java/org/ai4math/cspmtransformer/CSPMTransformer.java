package org.ai4math.cspmtransformer;

import org.ai4math.cspm.Keywords;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;
import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.ai4math.utils.CSPFileUtils;

import org.ai4math.cspmtransformer.utils.StringConstants;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.*;

import static org.ai4math.cspm.Keywords.LAMBDA;


public class CSPMTransformer {
    private List<String> cspFiles;
    private String currentCSPFile;
    private List<CSPVertex> traversed;
    private String path;
    private Map<String, String> types;
    private Map<String, String> typePlaceholders;

    public CSPMTransformer() {
        this.cspFiles = List.of();
        this.currentCSPFile = "";
        this.traversed = List.of();
        this.path = "";
        this.types = Map.of();
        this.typePlaceholders = Map.of();
    }
    public CSPMTransformer(String path) {
        this.cspFiles = List.of();
        this.currentCSPFile = "";
        this.traversed = List.of();
        this.path = path;
        this.types = Map.of();
        this.typePlaceholders = Map.of();
    }

    public void graphToCSPM (String path, CSPGraph graph, String filename) throws IOException{
        this.currentCSPFile = "";
        this.types = Map.of();
        this.typePlaceholders = Map.of();
        this.path = path;
        addChannelDefinitions(graph);
        List<CSPVertex> initialVertices = addProcessDefinitions(graph);
        addAssertion(initialVertices);
        replaceTypePlaceholders();
        formatCSPFilesOutput(filename);
    }

    public List<String> getCspFiles(boolean regen) throws IOException{
        List<String> files = new ArrayList<>(this.cspFiles);
        if (regen) {
            files.addAll(new CSPFileUtils().getCSPFiles(this.path));
        }
        this.cspFiles = files;
        return cspFiles;
    }

    private void formatCSPFilesOutput(String filename) throws IOException {
        List<String> files = new ArrayList<String>(this.cspFiles);

        files.add(new CSPFileUtils().createCSPFile(this.path,filename+".csp", this.currentCSPFile));

        this.cspFiles = files;
    }

    private void addChannelDefinitions(CSPGraph graph) {

        Map<String, String> channels = getChannels(graph);

        for (Map.Entry<String, String> channel: channels.entrySet()){
            if (!channel.getKey().isEmpty()) {
                if (channel.getValue() != null){
                    this.currentCSPFile += StringConstants.channelTypedDeclaration()
                            .replace("name", channel.getKey())
                            .replace("type", channel.getValue());
                } else {
                    this.currentCSPFile += StringConstants.channelDeclaration()
                            .replace("name", channel.getKey());
                }
            }
        }
    }

    private List<CSPVertex> addProcessDefinitions(CSPGraph graph) {
        List<CSPVertex> initialVertices = new ArrayList<>();
        for (CSPVertex vertex : graph.vertexSet()) {
            this.traversed = List.of();
            if (vertex.isInitialVertex() && vertex.isProcessVertex()) {
                String processDefinition = addProcessDefinition(vertex, graph, true) + "\n";
                String processName = vertex.getName();

                initialVertices.add(vertex);
                this.currentCSPFile += StringConstants.processDeclaration()
                        .replace("processName", processName)
                        .replace("processDefinition", processDefinition);
            }
        }

        return initialVertices;
    }

    private String addProcessDefinition(CSPVertex vertex, CSPGraph graph, boolean initial){
        StringBuilder processDefinition = new StringBuilder();
        Set<RelationshipEdge> vertexEdges = graph.outgoingEdgesOf(vertex);
        if (!vertex.getRenaming().isEmpty() && initial) {
            processDefinition.append("(");
        }
        if (!vertex.getHidden().isEmpty() && initial) {
            processDefinition.append("(");
        }
        if(traversed.contains(vertex)){
            return "";
        }
        else if (vertex.isStopVertex() || vertexEdges.isEmpty()){
            addTraversedVertex(vertex);
            return "";
        }
        else if (vertex.isExternalChoice()) {
            return choiceOrParallel(
                    processDefinition,
                    vertex,
                    graph,
                    vertexEdges,
                    initial,
                    0)
                    .toString();
        }
        else if (vertex.isInternalChoice()){
            return choiceOrParallel(
                    processDefinition,
                    vertex,
                    graph,
                    vertexEdges,
                    initial,
                    1)
                    .toString();
        }
        else if (vertex.isAlphabetisedParallel()){
            return choiceOrParallel(
                    processDefinition,
                    vertex,
                    graph,
                    vertexEdges,
                    initial,
                    2)
                    .toString();
        }
        else if (vertex.isGeneralisedParallel()){
            return choiceOrParallel(
                    processDefinition,
                    vertex,
                    graph,
                    vertexEdges,
                    initial,
                    3)
                    .toString();
        }
        else if (vertex.isInterleave()){
            return choiceOrParallel(
                    processDefinition,
                    vertex,
                    graph,
                    vertexEdges,
                    initial,
                    4)
                    .toString();
        }
        else if (vertex.isSeqCompositionVertex()){
            return sequentialComposition(
                    processDefinition,
                    vertex,
                    vertexEdges,
                    graph)
                    .toString();
        }
        else {
            addTraversedVertex(vertex);
            int edgeCount = 0;
            for (RelationshipEdge vertexEdge : vertexEdges) {
                CSPVertex targetVertex = graph.getEdgeTarget(vertexEdge);
                boolean seqCompVertex = targetVertex.isSeqCompositionVertex();
                if (seqCompVertex && !initial) {
                    processDefinition.append(addProcessDefinition(targetVertex, graph, false));
                } else if (vertex.isProcessVertex() && !initial) {
                    return processDefinition.toString();
                } else if (vertexEdge.getLabel() != null && !vertexEdge.getLabel().equals(LAMBDA) && !seqCompVertex) {
                    if (edgeCount > 0){
                        continue;
                    }
                    processDefinition.append(addEdgeDefinition(vertexEdge, targetVertex));
                    processDefinition.append(addProcessDefinition(targetVertex, graph, false));
                    edgeCount += 1;
                } // todo: add handling for tau
            }
        }
        if(!vertex.getHidden().isEmpty() && initial){
            return hidden(processDefinition, vertex).toString();
        }
        if(!vertex.getRenaming().isEmpty() && initial){
            return renaming(processDefinition, vertex).toString();
        }

        return processDefinition.toString();
    }

    private StringBuilder hidden(StringBuilder processDefinition, CSPVertex vertex){
        return processDefinition.append(")")
                .append("\\")
                .append(formatSet(vertex.getHidden()));
    }

    private StringBuilder renaming(StringBuilder processDefinition, CSPVertex vertex){
        processDefinition.append(")")
                .append("[[");

        vertex.getRenaming().forEach(
                (key,value) ->
                        processDefinition.append(key).
                                append("<-").append(value).
                                append(","));
        processDefinition.deleteCharAt(processDefinition.length()-1);
        return processDefinition.append("]]");
    }

    private StringBuilder sequentialComposition(StringBuilder processDefinition, CSPVertex vertex,
                                                Set<RelationshipEdge> vertexEdges, CSPGraph graph){
        addTraversedVertex(vertex);
        for (RelationshipEdge vertexEdge : vertexEdges) {
            CSPVertex targetVertex = graph.getEdgeTarget(vertexEdge);
            processDefinition.append("; ").append(addEdgeDefinition(vertexEdge, targetVertex))
                    .append(addProcessDefinition(targetVertex, graph, false));
        }

        return  processDefinition;
    }

    private StringBuilder interleave(StringBuilder processDefinition, RelationshipEdge vertexEdge,
                                     CSPVertex targetVertex, CSPVertex vertex, CSPGraph graph){
        // todo: add check for guards
        return processDefinition.append("(")
                .append(addEdgeDefinition(vertexEdge, targetVertex))
                .append(addProcessDefinition(targetVertex, graph, false))
                .append(") ||| ");

    }

    private StringBuilder generalisedParallel(StringBuilder processDefinition, RelationshipEdge vertexEdge,
                                              CSPVertex targetVertex, CSPVertex vertex, CSPGraph graph) {
        return processDefinition.append("(")
                .append(addEdgeDefinition(vertexEdge, targetVertex))
                .append(addProcessDefinition(targetVertex, graph, false))
                .append(") [| ").append(formatSet(vertex.getAlphabet().getFirst()))
                .append(" |] ");
    }

    private StringBuilder alphabetisedParallel(StringBuilder processDefinition, RelationshipEdge vertexEdge,
                                               CSPVertex targetVertex, CSPVertex vertex, CSPGraph graph){
        return processDefinition.append("(")
                .append(addEdgeDefinition(vertexEdge, targetVertex))
                .append(addProcessDefinition(targetVertex, graph, false))
                .append(") [ ")
                .append(formatSet(vertex.getAlphabet().getFirst()))
                .append(" || ")
                .append(formatSet(vertex.getAlphabet().getLast()))
                .append(" ] ");
    }

    private StringBuilder internalChoice(StringBuilder processDefinition, RelationshipEdge vertexEdge,
                                               CSPVertex targetVertex, CSPVertex vertex, CSPGraph graph){
        return processDefinition.append("(")
                        .append(addEdgeDefinition(vertexEdge, targetVertex))
                        .append(addProcessDefinition(targetVertex, graph, false))
                        .append(") |~| ");
    }

    private StringBuilder externalChoice(StringBuilder processDefinition, RelationshipEdge vertexEdge,
                                               CSPVertex targetVertex, CSPVertex vertex, CSPGraph graph){
        return processDefinition.append("(")
                .append(addEdgeDefinition(vertexEdge, targetVertex))
                .append(addProcessDefinition(targetVertex, graph, false))
                .append(") [] ");
    }

    private StringBuilder choiceOrParallel(StringBuilder processDefinition, CSPVertex vertex, CSPGraph graph,
                                           Set<RelationshipEdge> vertexEdges, boolean initial, int operator){
        addTraversedVertex(vertex);
        if (vertex.isProcessVertex() && !initial) {
            return processDefinition;
        }
        Iterator<RelationshipEdge> edges = vertexEdges.iterator();
        boolean incomplete = true;
        boolean seq = false;
        CSPVertex seqVertex = vertex;
        while(incomplete){
            RelationshipEdge vertexEdge = edges.next();
            CSPVertex targetVertex = graph.getEdgeTarget(vertexEdge);
            if (targetVertex.isSeqCompositionVertex()){
                seq = true;
                seqVertex = targetVertex;
            }
            if (edges.hasNext() && !targetVertex.isSeqCompositionVertex()) {
                // todo: add check for guards
                if (operator == 0){
                    processDefinition =
                            externalChoice(
                                    processDefinition,
                                    vertexEdge,
                                    targetVertex,
                                    vertex,
                                    graph);
                }
                if (operator == 1){
                    processDefinition =
                            internalChoice(
                                    processDefinition,
                                    vertexEdge,
                                    targetVertex,
                                    vertex,
                                    graph);
                }
                if (operator == 2){
                    processDefinition =
                            alphabetisedParallel(
                                    processDefinition,
                                    vertexEdge,
                                    targetVertex,
                                    vertex,
                                    graph);
                }
                if (operator == 3){
                    processDefinition =
                            generalisedParallel(
                                    processDefinition,
                                    vertexEdge,
                                    targetVertex,
                                    vertex,
                                    graph);
                }
                else if (operator == 4){
                    processDefinition =
                            interleave(
                                    processDefinition,
                                    vertexEdge,
                                    targetVertex,
                                    vertex,
                                    graph);
                }
            }
            else if (!edges.hasNext() && !targetVertex.isSeqCompositionVertex()){
                // todo: add check for guards
                processDefinition.append("(")
                        .append(addEdgeDefinition(vertexEdge, targetVertex))
                        .append(addProcessDefinition(targetVertex, graph, false))
                        .append(")");
                incomplete = false;
            }
            else if (!edges.hasNext() && targetVertex.isSeqCompositionVertex()){
                // todo: add check for guards
                processDefinition.append(")");
                incomplete = false;
            }
        }
        if (seq){
            processDefinition.append(addProcessDefinition(seqVertex, graph, false));
        }

        return processDefinition;
    }

    private String formatSet(Set<String> alphabet){
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(String.join(",", alphabet)).append("}");
        return sb.toString();
    }

    private String addEdgeDefinition(RelationshipEdge vertexEdge, CSPVertex targetVertex){
        String processTarget = targetVertex.getName();
        if (vertexEdge.getLabel().isEmpty()){
            return processTarget;
        }
        return vertexEdge.getLabel() + " -> " + processTarget;
    }

    private void addAssertion(List<CSPVertex> initialVertices){
        for (CSPVertex initialProcess : initialVertices) {
            String assertion = StringConstants.assertDeclaration()
                    .replace("assertion", StringConstants.deadlockAssertion());
            this.currentCSPFile += assertion.replace("process", initialProcess.getName());
        }
    }

    private Map<String, String> getChannels(CSPGraph graph){
        Map<String, String> channels = new HashMap<>();

        for (RelationshipEdge edge: graph.edgeSet()) {
            if (edge.getLabel() != null){
                System.out.println(edge.getLabel());
                String[] edgeComponents = edge.getLabel().split(" -> ");
                for (String component : edgeComponents) {
                    // "[a-zA-Z]*(;\\n)" would be a process, not a channel
                    if (component.matches("[a-zA-Z]*([!?$.]'?[a-zA-Z0-9]*'?)?")) {
                        String[] comps = component.splitWithDelimiters("[!?$.]",0);
                        if (!channels.containsKey(comps[0]) && comps.length>1) {
                            String type = getTypes(comps, false);
                            System.out.println("Adding typed channel: "+component+ ":"+type);
                            channels.put(comps[0], type);
                        }
                        else if(!channels.containsKey(comps[0])){
                            System.out.println("Adding channel: "+component);
                            channels.put(comps[0], null);
                        }
                    }

                    // todo: add parsing for guards
                }
            }
        }

        for (CSPVertex vertex: graph.vertexSet()){
            if (vertex.isAlphabetisedParallel() || vertex.isGeneralisedParallel()){
                for (Set<String> alphabet : vertex.getAlphabet()){
                    for (String channel : alphabet){
                        String[] comps = channel.splitWithDelimiters("[!?$.]",0);
                        if (!channels.containsKey(comps[0]) && comps.length>1) {
                            String type = getTypes(comps, true);
                            System.out.println("Adding typed alphabet channel: "+channel+ ":"+type);
                            channels.put(comps[0], type);
                        }
                        else if(!channels.containsKey(comps[0])){
                            System.out.println("Adding alphabet channel: "+channel);
                            channels.put(comps[0], null);
                        }
                    }
                }
            }
            if (!vertex.getHidden().isEmpty()){
                for (String channel: vertex.getHidden()){
                    /*String[] comps = channel.splitWithDelimiters("[!?$\\.]",0);
                    if (!channels.containsKey(comps[0]) && comps.length>1) {
                        String type = getTypes(comps, true);
                        System.out.println("Adding typed hidden channel: "+channel+ ":"+type);
                        channels.put(comps[0], type);
                    }
                    else*/
                    if(!channels.containsKey(channel)){
                        System.out.println("Adding hidden channel: "+channel);
                        channels.put(channel, null);
                    }
                }
            }
        }

        return channels;
    }

    private String getTypes(String[] components, boolean alphabet){
        String channel = components[0];
        String decoration = components[1];
        String parameter = components[2];
        Map<String, String> types = new HashMap<>(this.types);
        Random r = new Random();

        if (this.types.containsKey(channel)){
            return types.get(channel);
        }

        String type = randomType(parameter);
        if (type!=null) {
            types.put(channel, type);
        }
        else {
            type = RandomStringUtils.random(r.nextInt(2, 15), true, false);
            types.put(channel, type);

            String typeVal = randomDataType(parameter);

            this.currentCSPFile += StringConstants.dataTypeDeclaration()
                    .replace("name", type)
                    .replace("typeVal", typeVal);

        }

        /*if (Objects.equals(decoration, "!") || Objects.equals(decoration, ".")){
            String value = randomValue(type);

            if (value != null){
                this.currentCSPFile += StringConstants.constantDeclaration()
                    .replace("name", alphabet?RandomStringUtils.random(r.nextInt(12), true, false):parameter)
                    .replace("value", value);
            }
        }*/

        this.types = types;

        Map<String,String> typePlaceholders = new HashMap<>(this.typePlaceholders);
        typePlaceholders.put(components[0]+"."+Keywords.TYPE_PLACEHOLDER, components[0]+"."+randomValue(type));
        this.typePlaceholders = typePlaceholders;

        return type;
    }

    private String randomType(String value) {
        Random r = new Random();
        if (Objects.equals(value, "true") || Objects.equals(value, "false")) {
            return Keywords.BOOL;
        } else if (value.length()==3 &&
                Character.toString(value.charAt(0)).equals("'") &&
                Character.isAlphabetic(value.charAt(1))) {
            return Keywords.CHAR;
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            // This corresponds to Keywords.INT but the open integer range causes state explosion
            StringBuilder typeRange = new StringBuilder();
            int val = Integer.parseInt(value);
            int lowerBound = val>4?val - r.nextInt(6):val - r.nextInt(val+1);
            int upperBound = r.nextInt(lowerBound, lowerBound+10);
            typeRange.append("{")
                    .append(lowerBound)
                    .append("..")
                    .append(upperBound)
                    .append("}");
            return typeRange.toString();
        } else {
            return null;
        }
    }

    private String randomDataType(String value){
        Random r = new Random();
        List<String> types = new ArrayList<>();
        int typeCount = r.nextInt(1,10);
        for (int i = 0; i<typeCount; i++) {
            types.add(RandomStringUtils.random(r.nextInt(2, 15), true, false));
        }
        types.add(value);
        StringBuilder sb = new StringBuilder();
        return sb.append(String.join("|", types)).toString();
    }

    private String randomValue(String type){
        Random r = new Random();
        if (Objects.equals(type, Keywords.BOOL)){
            return String.valueOf(r.nextBoolean());
        }
        else if (Objects.equals(type, Keywords.CHAR)){
            StringBuilder sb = new StringBuilder();
            sb.append("'").append(RandomStringUtils.random(1, true, false)).append("'");
            return sb.toString();
        }
        else if (type.substring(0, 1).equals("{")){
            List<String> parts = new ArrayList<>(Arrays.stream(type.split("[.{}]")).toList());
            parts.removeIf(n -> Objects.equals(n,""));
            int lowerBound = Integer.parseInt(parts.get(0));
            int upperBound = Integer.parseInt(parts.get(1));
            return String.valueOf(r.nextInt(lowerBound, upperBound+1));
        } else {
            List<String> parts = new ArrayList<>(Arrays.stream(type.split("|")).toList());
            return parts.get(r.nextInt(0, parts.size()));
        }
    }

    private void replaceTypePlaceholders(){
        for (Map.Entry entry: this.typePlaceholders.entrySet()){
            this.currentCSPFile = this.currentCSPFile.replace(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    private void addTraversedVertex(CSPVertex vertex){
        List<CSPVertex> vertices = new ArrayList<>(this.traversed);
        vertices.add(vertex);
        this.traversed = vertices;
    }
}
