package org.ai4math.graphgenerator;


import org.ai4math.cspm.Keywords;
import org.ai4math.graphgenerator.utils.*;
import org.ai4math.utils.GraphGenerationOptions;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graphs;

import java.util.*;

import static java.util.Map.entry;
import static org.ai4math.cspm.Keywords.*;
import static org.ai4math.graphgenerator.utils.NameGenerator.*;

public class GraphGenerator {
    public List<CSPGraph> graphs;
    public NameVerifier nameVerifier;
    public Random random;
    public Boolean decorated;
    public Boolean renaming;
    public Boolean ver2;

    public GraphGenerator(GraphGenerationOptions ggo){
        this.graphs = List.of();
        this.nameVerifier = new NameVerifier();
        this.random = new Random();
        this.decorated = ggo.isDecorations();
        this.renaming = ggo.isRenaming();
        this.ver2 = ggo.isVersion2();
    }

    public List<CSPGraph> generateGraphSet(Integer baseGraphs, Integer combineGraphs) {
        // take parameters for generation and record those parameters
        // side note: make a tool to parse the csp files to get data about representation in the dataset
        //              versus representation in existing examples

        generateBaseGraphs(baseGraphs);
        combineGraphs(combineGraphs);

        return this.graphs;
    }

    public void generateBaseGraphs(int count){
        List<CSPGraph> baseGraphs = new ArrayList<>(this.graphs);
        int i = 0;

        while (i<count){
            CSPGraph baseGraph = new CSPGraph();

            String processName = generateProcessName(this.nameVerifier);

            List<String> messages = generateMessages(this.random, this.random.nextInt(30),
                    this.nameVerifier, this.decorated);
            String label = generateEdge(messages);

            CSPVertex initialProcess = new CSPVertex(processName, true, true);
            baseGraph.addVertex(initialProcess);

            CSPVertex process = generateProcess(messages, false, false, null);
            baseGraph.addVertex(process);
            RelationshipEdge e = baseGraph.addEdge(initialProcess, process);
            e.setLabel(label);

            if (!process.getName().equals(SKIP) && !process.getName().equals(STOP)){
                CSPGraph newGraph = generateGraph(process, messages, 15);
                if (!newGraph.vertexSet().isEmpty()) {
                    Graphs.addGraph(baseGraph, newGraph);
                }
            }
            baseGraphs.add(baseGraph);
            i++;
        }

        this.graphs = baseGraphs;
    }

    private CSPGraph generateGraph(CSPVertex process, List<String> messages, int limit){
        return generateGraph(process,messages,limit,"");
    }

    private CSPGraph generateGraph(CSPVertex process, List<String> messages, int limit, String edgeLabel){
        CSPGraph graph = new CSPGraph();

        if (limit == 0){
            return graph;
        }

        String label = edgeLabel.isEmpty()?generateEdge(messages):edgeLabel;

        process.setInitialVertex(true);
        graph.addVertex(process);

        CSPVertex newProcess = generateProcess(messages, false, limit == 1, null);
        graph.addVertex(newProcess);
        RelationshipEdge e = graph.addEdge(process, newProcess);
        e.setLabel(label);

        if (!newProcess.getName().equals(SKIP) && !newProcess.getName().equals(STOP)){
            CSPGraph newGraph = generateGraph(newProcess, messages, limit - 1);
            if (!newGraph.vertexSet().isEmpty()) {
                Graphs.addGraph(graph, newGraph);
            }
        }

        return graph;
    }

    private CSPVertex generateProcess(List<String> messages, boolean nonTerminal, boolean terminate, String type){
        CSPVertex process = new CSPVertex("temp");
        //int choice = this.random.nextInt(nonTerminal?1:0,terminate?2:3);
        int choice = this.random.nextInt(nonTerminal?2:0,terminate?2:3);
        if (choice == 0) {
            process.setName(STOP);
            process.setStopVertex(true);
        } else if (choice == 1) {
            process.setName(SKIP);
            process.setSkipVertex(true);
        } else if (choice == 2) {
            process = generateNonTerminalProcess(type);
        }

        int hidden = this.random.nextInt(0,30);
        if (hidden == 7) {
            Set<String> hiddenChannels = new HashSet<>(randomSubList(messages, true));
            process.setHidden(hiddenChannels);
        }
        if (renaming) {
            int renamingInt = this.random.nextInt(0, 30);
            if (renamingInt == 18) {
                Map<String, String> renamings = new LinkedHashMap<>();
                int counter = 0;
                while (renamings.isEmpty() && counter < 50) {
                    List<String> renameChannels = randomSubList(messages, false);
                    List<String> renamedChannels = randomSetSizeSubList(messages, renameChannels.size(), false);

                    for (int i = 0; i < renameChannels.size() - 1; i++) {
                        if (typeOf(renameChannels.get(i)) == (typeOf(renamedChannels.get(i)))) {
                            renamings.put(getDotted(renameChannels.get(i)),
                                    getDotted(renamedChannels.get(i)));
                        }
                    }
                    counter++;
                }
                process.setRenaming(renamings);
            }
        }

        if (type!=null){
            Pair<String, Pair<String,String>> typedprocess =
                    NameGenerator.generateTypedProcessName(random, nameVerifier, type);
            process.setName(typedprocess.getKey());
            process.setParameter(typedprocess.getValue());
        }

        return process;
    }

    private Map<CSPGraph,CSPVertex> generateNonTerminalGraph(CSPVertex process, List<String> messages, int limit){
        CSPGraph graph = new CSPGraph();

        if (limit == 0){
            return Map.ofEntries(entry(graph,process));
        }

        String label = generateEdge(messages);

        process.setInitialVertex(true);
        graph.addVertex(process);

        CSPVertex newProcess = generateProcess(messages, true, false, null);
        graph.addVertex(newProcess);
        RelationshipEdge e = graph.addEdge(process, newProcess);
        e.setLabel(label);

        Map<CSPGraph,CSPVertex> ntGraph = generateNonTerminalGraph(newProcess, messages, limit - 1);
        Map.Entry<CSPGraph,CSPVertex> newGraph = ntGraph.entrySet().iterator().next();
        if (!newGraph.getKey().vertexSet().isEmpty()) {
            Graphs.addGraph(graph, newGraph.getKey());
        }

        return Map.ofEntries(entry(graph, newGraph.getValue()));
    }

    private CSPVertex generateNonTerminalProcess(String type){
        CSPVertex process = new CSPVertex("temp");
        process.setName(generateProcessName(this.nameVerifier));
        process.setProcessVertex(true);

        if (type!=null){
            Pair<String, Pair<String,String>> typedprocess =
                    NameGenerator.generateTypedProcessName(random, nameVerifier, type);
            process.setName(typedprocess.getKey());
            process.setParameter(typedprocess.getValue());
        }

        return process;
    }

    private CSPGraph generateEndGraph(CSPGraph graph, CSPVertex process, List<String> messages, int limit){
        CSPGraph endGraph = new CSPGraph();

        if (limit == 0){
            return endGraph;
        }

        String label = generateEdge(messages);

        process.setInitialVertex(true);
        endGraph.addVertex(process);

        CSPVertex sourceVertex = getRandomVertex(graph, false, false);

        int choice = this.random.nextInt(2);
        if (choice == 0) {
            CSPVertex newProcess = generateProcess(messages, false, limit == 1, null);
            endGraph.addVertex(newProcess);
            RelationshipEdge e = endGraph.addEdge(process, newProcess);
            e.setLabel(label);

            if (!newProcess.getName().equals(SKIP) && !newProcess.getName().equals(STOP)) {
                CSPGraph newGraph = generateEndGraph(endGraph, newProcess, messages, limit - 1);
                if (!newGraph.vertexSet().isEmpty()) {
                    Graphs.addGraph(endGraph, newGraph);
                }
            }
        } else {
            endGraph.addVertex(sourceVertex);
            RelationshipEdge e = endGraph.addEdge(process, sourceVertex);
            e.setLabel(label);
        }

        return endGraph;
    }

    public void combineGraphs(int count){
        List<CSPGraph> baseGraphs = new ArrayList<>(this.graphs);
        List<CSPGraph> combinedGraphs = new ArrayList<>();
        int i = 0;

        while (i<count){
            CSPGraph sourceGraph = baseGraphs.get(this.random.nextInt(baseGraphs.size()));
            CSPGraph graph = new CSPGraph();

            int j = this.random.nextInt(1,6);
            for (int k = 0; k <= j; k++){
                generateCombinedGraph(sourceGraph, graph);
            }

            Graphs.addGraph(graph, sourceGraph);
            combinedGraphs.add(graph);
            i++;
        }

        baseGraphs.addAll(combinedGraphs);
        this.graphs = baseGraphs;
    }

    private CSPVertex getRandomVertex(CSPGraph graph, boolean nonTerminal, boolean seq){
        List<CSPVertex> vertexList = new ArrayList<>(graph.vertexSet().stream().toList());
        CSPVertex vertex = vertexList.get(this.random.nextInt(graph.vertexSet().size()));
        while ((vertex.isInterleave() || vertex.isAlphabetisedParallel() || vertex.isGeneralisedParallel() ||
                vertex.isInternalChoice() || vertex.isExternalChoice() || vertex.isSeqCompositionVertex())
                || (nonTerminal && (vertex.isSkipVertex() || vertex.isStopVertex()))
                || (seq && vertex.isStopVertex())) {
            if (vertexList.size()==1){
                return vertex;
            }
            vertexList.remove(vertex);
            vertex = vertexList.get(this.random.nextInt(0,vertexList.size()));
        }

        return vertex;
    }

    private void generateCombinedGraph(CSPGraph sourceGraph, CSPGraph graph) {
        List<String> messages = generateMessages(this.random,this.random.nextInt(30),
                this.nameVerifier, this.decorated);
        messages.addAll(getMessagesFromGraph(sourceGraph));

        CSPVertex combinationProcess = generateCombinationProcess(messages);
        graph.addVertex(combinationProcess);
        List<CSPVertex> vertexList = new ArrayList<>(graph.vertexSet().stream().toList());
        vertexList.remove(combinationProcess);

        if (combinationProcess.isSeqCompositionVertex()) {
            CSPVertex vertex = getRandomVertex(vertexList.isEmpty()?sourceGraph:graph, false, true);
            graph.addVertex(vertex);
            if (this.random.nextBoolean()) {
                generateStarterGraph(messages, combinationProcess, graph, true);
                RelationshipEdge f = graph.addEdge(combinationProcess, vertex);
                f.setLabel(generateEdge(messages));
            } else {
                RelationshipEdge e = graph.addEdge(vertex, combinationProcess);
                e.setLabel(TICK);
                CSPVertex newProcess = generateNonTerminalProcess(null);
                graph.addVertex(newProcess);
                RelationshipEdge f = graph.addEdge(combinationProcess, newProcess);
                f.setLabel(generateEdge(messages));
                CSPGraph newGraph = generateGraph(newProcess, messages, 4);
                Graphs.addGraph(graph, newGraph);
            }
        } else if (combinationProcess.getReplicatedOperator()!=null)
        {
            CSPVertex vertex = getRandomVertex(vertexList.isEmpty()?sourceGraph:graph, true, false);
            graph.addVertex(vertex);
            if (this.random.nextBoolean()) {
                generateStarterGraph(messages, combinationProcess, graph, false);
            }
            RelationshipEdge e = graph.addEdge(vertex, combinationProcess);
            e.setLabel(generateEdge(messages));
            CSPVertex newProcess = generateNonTerminalProcess(combinationProcess.getRepOpType());
            graph.addVertex(newProcess);
            RelationshipEdge f = graph.addEdge(combinationProcess, newProcess);
            f.setLabel(LAMBDA);
            CSPGraph newGraph = generateGraph(newProcess, messages, 4);
            Graphs.addGraph(graph, newGraph);
        } else if (combinationProcess.isInternalChoice() || combinationProcess.isExternalChoice()
                || combinationProcess.isInterleave() || combinationProcess.isAlphabetisedParallel()
                || combinationProcess.isGeneralisedParallel() || combinationProcess.isTimeout()
                || combinationProcess.isException() || combinationProcess.isInterrupt() ) {
            // need at least two processes connected to the combinationProcess, either with or without messages
            // todo: currently restricted to two edges

            CSPVertex vertex = getRandomVertex(vertexList.isEmpty()?sourceGraph:graph, false, false);
            graph.addVertex(vertex);
            if (this.random.nextBoolean()) {
                generateStarterGraph(messages, combinationProcess, graph, false);
            }
            RelationshipEdge e = graph.addEdge(combinationProcess, vertex);
            Boolean guarded = this.random.nextInt(7,9)==8 &&
                    combinationProcess.isExternalChoice();
            List<String> edges = List.of();
            if (guarded && this.ver2 && combinationProcess.getParameter() != null){
                edges = generateGuardedEdges(messages,combinationProcess.getParameter());
            }
            e.setLabel(edges.isEmpty()?generateEdge(messages):edges.get(0));
            CSPGraph extraGraph = generateGraph(combinationProcess, messages, 4,
                    edges.isEmpty()?"":edges.get(1));
            if (!extraGraph.vertexSet().isEmpty()) {
                Graphs.addGraph(graph, extraGraph);
            }
        }
    }

    private void generateStarterGraph(List<String> messages, CSPVertex combinationProcess,
                                          CSPGraph graph, boolean seqComp){
        // adds an extra graph before the combination process
        CSPVertex newInitProcess =
                new CSPVertex(generateProcessName(this.nameVerifier),
                        true, true);
        Map<CSPGraph, CSPVertex> ntGraph = generateNonTerminalGraph(newInitProcess, messages, 4);
        Map.Entry<CSPGraph, CSPVertex> ntEntry = ntGraph.entrySet().iterator().next();
        CSPGraph newGraph = ntEntry.getKey();
        CSPVertex finalVertex = ntEntry.getValue().isSkipVertex()||ntEntry.getValue().isStopVertex()?
                getRandomVertex(newGraph, !seqComp, seqComp):ntEntry.getValue();
        if (!newGraph.vertexSet().isEmpty()) {
            Graphs.addGraph(graph, newGraph);

            RelationshipEdge e = graph.addEdge(finalVertex, combinationProcess);
            if (seqComp) e.setLabel(TICK);
            else { e.setLabel(generateEdge(messages)); }

            CSPGraph endGraph = generateEndGraph(graph, finalVertex, messages, 3);
            Graphs.addGraph(graph, endGraph);
        }
    }

    private CSPVertex generateCombinationProcess(List<String> messages){
        CSPVertex process = new CSPVertex("temp");
        int upperbound = this.ver2?19:6;
        int choice = this.random.nextInt(0,upperbound);
        if (choice == 0 || choice == 10) {
            // sequential composition
            process.setName("Sequential Composition");
            process.setProcessVertex(true);
            process.setSeqCompositionVertex(true);
        } else if (choice == 1 || choice == 11) {
            // internal choice
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setInternalChoice(true);
        } else if (choice == 2 || choice == 12) {
            // external choice
            if(this.random.nextBoolean()){
                Pair<String, Pair<String,String>> parameterProcess = generateTypedProcessName(this.random,this.nameVerifier);
                process.setName(parameterProcess.getKey());
                process.setParameter(parameterProcess.getValue());
            } else {
                process.setName(generateProcessName(this.nameVerifier));
            }
            process.setProcessVertex(true);
            process.setExternalChoice(true);
        } else if (choice == 3 || choice == 13) {
            // generalised parallel
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setGeneralisedParallel(true);
            Set<String> alphabetA = new HashSet<>(randomSubList(messages, true));
            List<Set<String>> alphabet = List.of(alphabetA);
            process.setAlphabet(alphabet);
        } else if (choice == 4 || choice == 14) {
            // alphabetised parallel
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setAlphabetisedParallel(true);
            Set<String> alphabetA = new HashSet<>(randomSubList(messages, true));
            Set<String> alphabetB = new HashSet<>(randomSubList(messages, true));
            List<Set<String>> alphabet = List.of(alphabetA, alphabetB);
            process.setAlphabet(alphabet);
        } else if (choice == 5 || choice == 15) {
            // interleave
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setInterleave(true);
        } else if (choice == 6 || choice == 16) {
            // interrupt
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setInterrupt(true);
        } else if (choice == 7 || choice == 17) {
            // exception
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setException(true);
            Set<String> alphabetA = new HashSet<>(randomSubList(messages, true));
            List<Set<String>> alphabet = List.of(alphabetA);
            process.setAlphabet(alphabet);
        } else if (choice == 8 || choice == 18) {
            // timeout
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setTimeout(true);
        } else if (choice == 9) {
            // replicated operators
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setInitialVertex(true); // a replicated operator defines an entire combination process
            CSPVertex.RepOp variant = Arrays.stream(CSPVertex.RepOp.values()).toList()
                    .get(random.nextInt(0, CSPVertex.RepOp.values().length));
            process.setReplicatedOperator(variant);
            String type = Keywords.INT; //generateType(random, this.nameVerifier);
            process.setRepOpType(type);
            if (process.getReplicatedOperator().equals(CSPVertex.RepOp.AlphParallel)
                    || process.getReplicatedOperator().equals(CSPVertex.RepOp.GenParallel)) {
                Set<String> alphabet = new HashSet<>(randomSubList(messages, true));
                process.setAlphabet(List.of(alphabet));
            }
        }

        return process;
    }

    private String generateEdge(List<String> messages){
        List<String> messagesTrimmed = new ArrayList<>(messages);
        messagesTrimmed.removeIf(n-> Objects.equals(n, "")); // remove empty strings from this
        if (!messagesTrimmed.isEmpty()) {
            return String.join(" -> ", randomSubList(messagesTrimmed, false));
        }
        return "";
    }

    private List<String> generateGuardedEdges(List<String> messages, Pair<String,String> parameter){
        List<String> edges = new ArrayList<>();
        List<String> messagesTrimmed = new ArrayList<>(messages);
        messagesTrimmed.removeIf(n-> Objects.equals(n, "")); // remove empty strings from this
        if (!messagesTrimmed.isEmpty()) {
            List<String> guard = generateGuardPair(nameVerifier, random, parameter.getKey(), parameter.getValue());
            for (int i=0; i < 2; i++){
                StringBuilder sb = new StringBuilder();
                sb.append(guard.get(i)).append("&(");
                sb.append(String.join(" -> ", randomSubList(messagesTrimmed, false)));
                sb.append(")");
                edges.add(sb.toString());
            }

            return edges;
        }
        return List.of();
    }

    private List<String> getMessagesFromGraph(CSPGraph graph){
        List<String> messages = new ArrayList<>();

        for (RelationshipEdge edge: graph.edgeSet()) {
            if (edge.getLabel() != null){
                String[] edgeComponents = edge.getLabel().split(" -> ");
                for (String component : edgeComponents) {
                    if (component.matches("[a-zA-Z]*([!?.]'?[a-zA-Z0-9]*'?)?")) {
                        if(!messages.contains(component)){
                            messages.add(component);
                        }
                    }
                }
            }
        }

        return messages;
    }

    public List<String> randomSubList(List<String> list, boolean alphabet) {
        list.removeIf(n->Objects.equals(n,""));
        if (list.size()==1){
            return alphabet?List.of(getDotted(list.getFirst())):list;
        } else if (list.isEmpty()){
            return list;
        }

        int newSize = this.random.nextInt(1, list.size());
        return getStrings(list, newSize, alphabet);
    }

    public static List<String> randomSetSizeSubList(List<String> list, int size, boolean alphabet) {
        list.removeIf(n->Objects.equals(n,""));
        if (list.size()==1){
            return alphabet?List.of(getDotted(list.getFirst())):list;
        } else if (list.isEmpty()){
            return list;
        }
        if (size > list.size()) {size = list.size();}
        return getStrings(list, size, alphabet);
    }

    private static List<String> getStrings(List<String> list, int size, boolean alphabet) {
        List<String> shuffleList = new ArrayList<>(list);
        Collections.shuffle(shuffleList);
        List<String> sublist = shuffleList.subList(0, size);
        if (alphabet){
            List<String> sublistCopy = new ArrayList<>();
            for(String item: sublist) {
                sublistCopy.add(getDotted(item));
            }
            sublist = sublistCopy;
        }
        return sublist;
    }

    private static String getDotted(String item){
        String[] items = item.split("[!?$.]");
        StringBuilder sb = new StringBuilder();
        sb.append(items[0]);
        if (items.length>1) {
            sb.append(".").append(items[1]);
        }
        return sb.toString();
    }

    private static String typeOf(String message){
        String[] comps = message.split("[!?$.]",0);
        if (comps.length>1) {
            String value = comps[1];
            if (Objects.equals(value, "true") || Objects.equals(value, "false")) {
                return Keywords.BOOL;
            } else if (value.length() == 3 &&
                    Character.toString(value.charAt(0)).equals("'") &&
                    Character.isAlphabetic(value.charAt(1))) {
                return Keywords.CHAR;
            } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                return Keywords.INT;
            }
        }

        return null;
    }

    public List<CSPGraph> getGraphs() {
        return graphs;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void addGraph(CSPGraph graph){
        if (this.graphs == null){
            this.graphs = List.of(graph);
        } else {
            List<CSPGraph> graphs = new ArrayList<>(this.graphs);
            graphs.add(graph);
            this.graphs = graphs;
        }

    }
}
