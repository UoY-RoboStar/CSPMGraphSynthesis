package org.ai4math.cspmtransformer;

import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;
import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.ai4math.utils.CSPFileUtils;

import org.ai4math.cspmtransformer.utils.StringConstants;
import org.apache.commons.math3.random.StableRandomGenerator;

import java.util.*;

import static org.ai4math.cspm.Keywords.LAMBDA;


// differentiates files for syntactical equivalence (R;P);Q and R;(P;Q)
public class CSPMTransformer {
    private List<String> cspFiles;
    private String currentCSPFile;
    private List<CSPVertex> traversed;

    public CSPMTransformer(){
        List<String> files = new ArrayList<String>();
        //files.addAll(new CSPFileUtils().getCSPFiles());
        this.cspFiles = files;
        this.currentCSPFile = "";
        this.traversed = List.of();
    }

    public void graphToCSPM (CSPGraph graph, String filename){
        this.currentCSPFile = "";
        addChannelDefinitions(graph);
        List<CSPVertex> initialVertices = addProcessDefinitions(graph);
        addAssertion(initialVertices);
        formatCSPFilesOutput(filename);
    }

    public List<String> getCspFiles() {
        return cspFiles;
    }

    private void formatCSPFilesOutput(String filename){
        List<String> files = new ArrayList<String>(this.cspFiles);

        files.add(new CSPFileUtils().createCSPFile(filename+".csp", this.currentCSPFile));

        this.cspFiles = files;
    }

    private void addChannelDefinitions(CSPGraph graph) {

        List<String> channels = getChannels(graph);

        for (String channel: channels){
            if (!channel.isEmpty()) {
                this.currentCSPFile += StringConstants.channelDeclaration().replace("name", channel);
            }
        }

        // todo: add definitions for typed channels, need to determine type based on the var somehow and then
        // specify the datatype if needed, or define a constant if a literal value

    }

    private List<CSPVertex> addProcessDefinitions(CSPGraph graph) {
        List<CSPVertex> initialVertices = new ArrayList<>();
        for (CSPVertex vertex : graph.vertexSet()) {
            this.traversed = List.of();
            if (vertex.isInitialVertex() && vertex.isProcessVertex()) {
                String processDefinition = addProcessDefinition(vertex, graph, true) + "\n";
                String processName = vertex.getName();

                initialVertices.add(vertex);
                String processDeclaration = StringConstants.processDeclaration().replace("processName", processName);
                this.currentCSPFile += processDeclaration.replace("processDefinition", processDefinition);
            }
        }

        return initialVertices;
    }

    private String addProcessDefinition(CSPVertex vertex, CSPGraph graph, boolean initial){
        StringBuilder processDefinition = new StringBuilder();
        Set<RelationshipEdge> vertexEdges = graph.outgoingEdgesOf(vertex);
        if(!vertex.getHidden().isEmpty() && initial) {
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
            addTraversedVertex(vertex);
            if (vertex.isProcessVertex() && !initial) {
                return processDefinition.toString();
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
                    processDefinition.append("(")
                            .append(addEdgeDefinition(vertexEdge, targetVertex))
                            .append(addProcessDefinition(targetVertex, graph, false))
                            .append(") [] ");
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
            return processDefinition.toString();
        }
        else if (vertex.isInternalChoice()){
            addTraversedVertex(vertex);
            if (vertex.isProcessVertex() && !initial) {
                return processDefinition.toString();
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
                    processDefinition.append("(")
                            .append(addEdgeDefinition(vertexEdge, targetVertex))
                            .append(addProcessDefinition(targetVertex, graph, false))
                            .append(") |~| ");
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
            return processDefinition.toString();
        }
        else if (vertex.isAlphabetisedParallel()){
            addTraversedVertex(vertex);
            if (vertex.isProcessVertex() && !initial) {
                return processDefinition.toString();
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
                    processDefinition.append(" (")
                            .append(addEdgeDefinition(vertexEdge, targetVertex))
                            .append(addProcessDefinition(targetVertex, graph, false))
                            .append(") [ ")
                            .append(formatSet(vertex.getAlphabet().getFirst()))
                            .append(" || ")
                            .append(formatSet(vertex.getAlphabet().getLast()))
                            .append(" ] ");
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
            return processDefinition.toString();
        }
        else if (vertex.isGeneralisedParallel()){
            addTraversedVertex(vertex);
            if (vertex.isProcessVertex() && !initial) {
                return processDefinition.toString();
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
                    processDefinition.append("(")
                            .append(addEdgeDefinition(vertexEdge, targetVertex))
                            .append(addProcessDefinition(targetVertex, graph, false))
                            .append(") [| ").append(formatSet(vertex.getAlphabet().getFirst()))
                            .append(" |] ");
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
            return processDefinition.toString();
        }
        else if (vertex.isInterleave()){
            addTraversedVertex(vertex);
            if (vertex.isProcessVertex() && !initial) {
                return processDefinition.toString();
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
                    processDefinition.append("(")
                            .append(addEdgeDefinition(vertexEdge, targetVertex))
                            .append(addProcessDefinition(targetVertex, graph, false))
                            .append(") ||| ");
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
            return processDefinition.toString();
        }
        else if (vertex.isSeqCompositionVertex()){
            addTraversedVertex(vertex);
            for (RelationshipEdge vertexEdge : vertexEdges) {
                CSPVertex targetVertex = graph.getEdgeTarget(vertexEdge);
                //if (processDefinition.length() == 0){
                //    processDefinition += addEdgeDefinition(vertexEdge, targetVertex);
                //} else {
                processDefinition.append("; ").append(addEdgeDefinition(vertexEdge, targetVertex))
                        .append(addProcessDefinition(targetVertex, graph, false));
                //}
            }
            return processDefinition.toString();
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
            processDefinition.append(")")
                    .append("\\")
                    .append(formatSet(vertex.getHidden()));
        }

        return processDefinition.toString();
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


    private boolean lambdaEdgeCheck(CSPGraph graph, CSPVertex vertex, String initialVertex){
        Set<RelationshipEdge> edges = graph.edgesOf(vertex);
        for (RelationshipEdge edge: edges){
            if (edge.getLabel().equals(StringConstants.lambdaEdge().replace("processName", initialVertex))){
                return true;
            }
        }

        return false;
    }

    private void addAssertion(List<CSPVertex> initialVertices){
        for (CSPVertex initialProcess : initialVertices) {
            String assertion = StringConstants.assertDeclaration()
                    .replace("assertion", StringConstants.deadlockAssertion());
            this.currentCSPFile += assertion.replace("process", initialProcess.getName());
        }
    }

    private List<String> getChannels(CSPGraph graph){
        List<String> channels = new ArrayList<>();

        for (RelationshipEdge edge: graph.edgeSet()) {
            if (edge.getLabel() != null){
                System.out.println(edge.getLabel());
                String[] edgeComponents = edge.getLabel().split(" -> ");
                for (String component : edgeComponents) {
                    // "[a-zA-Z]*(;\\n)" would be a process, not a channel
                    if (component.matches("[a-zA-Z]*")) {
                        if(!channels.contains(component)){
                            System.out.println("Adding channel: "+component);
                            channels.add(component);
                        }
                    }

                    // todo: add parsing for channels with var passing so "[a-zA-Z]*![a-zA-Z0-9]*" and guards
                }
            }
        }

        for (CSPVertex vertex: graph.vertexSet()){
            if (vertex.isAlphabetisedParallel() || vertex.isGeneralisedParallel()){
                for (Set<String> alphabet : vertex.getAlphabet()){
                    for (String channel : alphabet){
                        if(!channels.contains(channel)){
                            System.out.println("Adding alphabet channel:" +channel);
                            channels.add(channel);
                        }
                    }
                }
            }
            if (!vertex.getHidden().isEmpty()){
                for (String channel: vertex.getHidden()){
                    if (!channels.contains(channel)){
                        System.out.println("Adding hidden channel: "+ channel);
                        channels.add(channel);
                    }
                }
            }
        }

        return channels;
    }

    private void addTraversedVertex(CSPVertex vertex){
        List<CSPVertex> vertices = new ArrayList<>(this.traversed);
        vertices.add(vertex);
        this.traversed = vertices;
    }
}
