package org.ai4math.graphgenerator;


import org.ai4math.graphgenerator.utils.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.jgrapht.Graphs;

import java.util.*;

import static java.util.Map.entry;
import static org.ai4math.cspm.Keywords.*;
import static org.ai4math.graphgenerator.utils.NameGenerator.generateMessages;
import static org.ai4math.graphgenerator.utils.NameGenerator.generateProcessName;

public class GraphGenerator {
    public List<CSPGraph> graphs;
    public NameVerifier nameVerifier;

    public GraphGenerator(){
        this.graphs = List.of();
        this.nameVerifier = new NameVerifier();
    }

    public List<CSPGraph> generateGraphSet(Integer baseGraphs, Integer combineGraphs) {
        // take parameters for generation and record those parameters
        // side note: make a tool to parse the csp files to get data about representation in the dataset
        //              versus representation in existing examples

        List<CSPGraph> graphs = new ArrayList<>();
        /*graphs.add(BasicGraph());
        graphs.add(CompositionalGraph());
        graphs.add(ParallelGraph());
        graphs.add(LoopGraph());*/

        generateBaseGraphs(baseGraphs);
        combineGraphs(combineGraphs);

        // Add graph combination step

        return this.graphs;
    }

    public void generateBaseGraphs(int count){
        List<CSPGraph> baseGraphs = new ArrayList<>(this.graphs);
        int i = 0;
        Random r = new Random();

        while (i<count){
            CSPGraph baseGraph = new CSPGraph();

            String processName = generateProcessName(this.nameVerifier);

            List<String> messages = generateMessages(r.nextInt(30), this.nameVerifier);
            String label = generateEdge(messages);

            CSPVertex initialProcess = new CSPVertex(processName, true, true);
            baseGraph.addVertex(initialProcess);

            CSPVertex process = generateProcess(messages);
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
        CSPGraph graph = new CSPGraph();

        if (limit == 0){
            return graph;
        }

        String label = generateEdge(messages);

        process.setInitialVertex(true);
        graph.addVertex(process);

        CSPVertex newProcess = generateProcess(messages);
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

    private CSPVertex generateProcess(List<String> messages){
        Random r = new Random();

        CSPVertex process = new CSPVertex("temp");
        int choice = r.nextInt(0,3);
        if (choice == 0) {
            process.setName(STOP);
            process.setStopVertex(true);
        } else if (choice == 1) {
            process.setName(SKIP);
            process.setStopVertex(true);
        } else if (choice == 2) {
            process = generateNonTerminalProcess();
        }

        int hidden = r.nextInt(0,20);
        if (hidden == 7) {
            Set<String> hiddenChannels = new HashSet<>(randomSubList(messages));
            process.setHidden(hiddenChannels);
        }
        /*int renaming = r.nextInt(0,20);
        if (renaming == 18) {
            List<String> renameChannels = randomSubList(messages);
            List<String> renamedChannels = randomSubList(messages);
            Map<String, String> renamings = new TreeMap<>();
            for (int i = 0; i < renameChannels.size(); i++) {
                renamings.put(renameChannels.get(i), renamedChannels.get(i));
            }
            process.setRenaming(renamings);
        }*/

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

        CSPVertex newProcess = generateNonTerminalProcess();
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

    private CSPVertex generateNonTerminalProcess(){
        CSPVertex process = new CSPVertex("temp");
        process.setName(generateProcessName(this.nameVerifier));
        process.setProcessVertex(true);
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

        CSPVertex sourceVertex = getRandomVertex(graph);

        Random r = new Random();
        int choice = r.nextInt(2);
        if (choice == 0) {
            CSPVertex newProcess = generateProcess(messages);
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
        Random r = new Random();
        int i = 0;

        while (i<count){
            CSPGraph sourceGraph = baseGraphs.get(r.nextInt(baseGraphs.size()));
            CSPGraph graph = new CSPGraph();

            int j = r.nextInt(1,6);
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

    private CSPVertex getRandomVertex(CSPGraph graph){
        Random r = new Random();

        CSPVertex vertex = graph.vertexSet().stream().toList()
                .get(r.nextInt(graph.vertexSet().size()));
        while (vertex.isInterleave() || vertex.isAlphabetisedParallel() || vertex.isGeneralisedParallel() ||
                vertex.isInternalChoice() || vertex.isExternalChoice() || vertex.isSeqCompositionVertex()){
            vertex = graph.vertexSet().stream().toList()
                    .get(r.nextInt(graph.vertexSet().size()));
        }

        return vertex;
    }

    private void generateCombinedGraph(CSPGraph sourceGraph, CSPGraph graph) {
        Random r = new Random();
        List<String> messages = generateMessages(r.nextInt(30), this.nameVerifier);
        messages.addAll(getMessagesFromGraph(sourceGraph));

        CSPVertex vertex = getRandomVertex(graph.vertexSet().isEmpty()? sourceGraph:graph);
        graph.addVertex(vertex);

        CSPVertex combinationProcess = generateCombinationProcess(messages);
        graph.addVertex(combinationProcess);

        if (combinationProcess.isSeqCompositionVertex()) {
            /*if (r.nextBoolean()) {
                // add transition from source to seqcomp, then add tick transition to new graph from seqcomp
                RelationshipEdge e = graph.addEdge(vertex, combinationProcess);
                e.setLabel(TICK);
                CSPGraph newGraph = generateGraph(combinationProcess, messages, 4);
                Graphs.addGraph(graph, newGraph);
                // define graph for seqcomp process
                //generateGraph();
            } else {*/
                // add new graph, then add tick transition to seqcomp, then add sourcegraph

                // define graph for seqcomp process
                if (r.nextBoolean()) {
                    generateStarterGraph(messages, combinationProcess, graph, true);
                    RelationshipEdge f = graph.addEdge(combinationProcess, vertex);
                    f.setLabel(generateEdge(messages));
                } else {
                    RelationshipEdge e = graph.addEdge(vertex, combinationProcess);
                    e.setLabel(TICK);
                    CSPVertex newProcess = generateNonTerminalProcess();
                    graph.addVertex(newProcess);
                    RelationshipEdge f = graph.addEdge(combinationProcess, newProcess);
                    f.setLabel(generateEdge(messages));
                    CSPGraph newGraph = generateGraph(newProcess, messages, 4);
                    Graphs.addGraph(graph, newGraph);
                }
            //}
        } else if (combinationProcess.isInternalChoice() || combinationProcess.isExternalChoice() || combinationProcess.isInterleave()
                || combinationProcess.isAlphabetisedParallel() || combinationProcess.isGeneralisedParallel()) {
            // need at least two processes connected to the combinationProcess, either with or without messages
            // could add guards on these also
            // todo: currently restricted to two edges
            //if (r.nextBoolean()) {
                if (r.nextBoolean()) {
                    generateStarterGraph(messages, combinationProcess, graph, false);
                }
                RelationshipEdge e = graph.addEdge(combinationProcess, vertex);
                e.setLabel(generateEdge(messages));
                CSPGraph extraGraph = generateGraph(combinationProcess, messages, 4);
                //CSPVertex extraVertex = extraGraph.getInitialVertex();
                if (!extraGraph.vertexSet().isEmpty()) {
                    Graphs.addGraph(graph, extraGraph);
                    //RelationshipEdge f = graph.addEdge(combinationProcess, extraVertex);
                    //f.setLabel(generateEdge(messages));
                }
            //}
        }
    }

    private void generateStarterGraph(List<String> messages, CSPVertex combinationProcess,
                                          CSPGraph graph, boolean seqComp){
        // adds an extra graph before the combination process
        CSPVertex newInitProcess =
                new CSPVertex(generateProcessName(this.nameVerifier),
                        true, true);
        Map<CSPGraph, CSPVertex> ntGraph = generateNonTerminalGraph(newInitProcess, messages, 4);
        // todo: need a new function that allows a SKIP for seq comp
        Map.Entry<CSPGraph, CSPVertex> ntEntry = ntGraph.entrySet().iterator().next();
        CSPGraph newGraph = ntEntry.getKey();
        CSPVertex finalVertex = ntEntry.getValue();
        if (!newGraph.vertexSet().isEmpty()) {
            Graphs.addGraph(graph, newGraph);

            RelationshipEdge e = graph.addEdge(finalVertex, combinationProcess);
            if (seqComp) e.setLabel(TICK);
            else { e.setLabel(generateEdge(messages)); }

            CSPGraph endGraph = generateEndGraph(graph, finalVertex, messages, 3);
            Graphs.addGraph(graph, endGraph);
        }

        //return graph;
    }

    private CSPVertex generateCombinationProcess(List<String> messages){
        Random r = new Random();

        CSPVertex process = new CSPVertex("temp");
        int choice = r.nextInt(0,6);
        if (choice == 0) {
            // sequential composition
            process.setName("Sequential Composition");
            process.setProcessVertex(true);
            process.setSeqCompositionVertex(true);
        } else if (choice == 1) {
            // internal choice
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setInternalChoice(true);
        } else if (choice == 2) {
            // external choice
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setExternalChoice(true);
        } else if (choice == 3) {
            // generalised parallel
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setGeneralisedParallel(true);
            Set<String> alphA = new HashSet<>(randomSubList(messages));
            List<Set<String>> alphabet = List.of(alphA);
            process.setAlphabet(alphabet);
        } else if (choice == 4) {
            // alphabetised parallel
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setAlphabetisedParallel(true);
            Set<String> alphA = new HashSet<>(randomSubList(messages));
            Set<String> alphB = new HashSet<>(randomSubList(messages));
            List<Set<String>> alphabet = List.of(alphA, alphB);
            process.setAlphabet(alphabet);
        } else if (choice == 5) {
            // interleave
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setInterleave(true);
        }

        return process;
    }

    private String generateEdge(List<String> messages){
        //List<String> messagesTrimmed = messages. // remove empty strings from this
        if (!messages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.join(" -> ", randomSubList(messages)));
            return sb.toString();
        }
        return "";
    }

    private List<String> getMessagesFromGraph(CSPGraph graph){
        List<String> messages = new ArrayList<>();

        for (RelationshipEdge edge: graph.edgeSet()) {
            if (edge.getLabel() != null){
                String[] edgeComponents = edge.getLabel().split(" -> ");
                for (String component : edgeComponents) {
                    if (component.matches("[a-zA-Z]*")) {
                        if(!messages.contains(component)){
                            messages.add(component);
                        }
                    }
                }
            }
        }

        return messages;
    }

    public static <T> List<T> randomSubList(List<T> list) {
        //list.remove("");
        if (list.size()<2){return list;}

        Random r = new Random();
        int newSize = r.nextInt(1, list.size());
        List<T> shuffleList = new ArrayList<>(list);
        Collections.shuffle(shuffleList);
        return shuffleList.subList(0, newSize);
    }

    public static <T> List<T> randomSetSizeSubList(List<T> list, int size) {
        //list.remove("");
        if (list.size()<2){return list;}

        List<T> shuffleList = new ArrayList<>(list);
        Collections.shuffle(shuffleList);
        return shuffleList.subList(0, size);
    }

    public CSPGraph basicGraph() {
        CSPGraph baseGraph = new CSPGraph();

        CSPVertex initialProcess = new CSPVertex("BasicProcess", true, true);
        CSPVertex stopProcess = new CSPVertex(STOP);
        stopProcess.setStopVertex(true);


        baseGraph.addVertex(initialProcess);
        baseGraph.addVertex(stopProcess);
        RelationshipEdge e = baseGraph.addEdge(initialProcess, stopProcess);
        e.setLabel("secondTestingChannel -> testingChannel");

        return baseGraph;
    }

    public CSPGraph compositionalGraph() {
        CSPGraph baseGraph = new CSPGraph();

        CSPVertex initialProcess = new CSPVertex("CompProcess", true, true);
        CSPVertex skipProcess = new CSPVertex(SKIP);
        skipProcess.setSkipVertex(true);

        baseGraph.addVertex(initialProcess);
        baseGraph.addVertex(skipProcess);
        RelationshipEdge e = baseGraph.addEdge(initialProcess, skipProcess);
        e.setLabel("secondTestingChannel -> testingChannel");

        CSPVertex compVertex = new CSPVertex("comp");
        compVertex.setSeqCompositionVertex(true);
        baseGraph.addVertex(compVertex);

        RelationshipEdge e2 = baseGraph.addEdge(skipProcess, compVertex);
        e2.setLabel(TICK);

        CSPVertex stopProcess = new CSPVertex(STOP);
        stopProcess.setStopVertex(true);

        baseGraph.addVertex(stopProcess);
        RelationshipEdge e3 = baseGraph.addEdge(compVertex, stopProcess);
        e3.setLabel("b -> d -> e");

        return baseGraph;
    }

    public CSPGraph parallelGraph() {
        CSPGraph baseGraph = new CSPGraph();

        CSPVertex initialProcess = new CSPVertex("ParProcess", true, true);

        initialProcess.setGeneralisedParallel(true);
        Set<String> alp = Set.of("a", "e");
        List<Set<String>> alphabet = List.of(alp);
        initialProcess.setAlphabet(alphabet);

        CSPVertex skipProcess = new CSPVertex(SKIP);
        skipProcess.setSkipVertex(true);
        CSPVertex stopProcess = new CSPVertex(STOP);
        stopProcess.setStopVertex(true);

        baseGraph.addVertex(initialProcess);
        baseGraph.addVertex(stopProcess);
        baseGraph.addVertex(skipProcess);

        RelationshipEdge e = baseGraph.addEdge(initialProcess, skipProcess);
        e.setLabel("a -> b");
        RelationshipEdge b = baseGraph.addEdge(initialProcess, stopProcess);
        b.setLabel("c -> e");

        return baseGraph;
    }

    public CSPGraph loopGraph() {
        CSPGraph baseGraph = new CSPGraph();
        CSPGraph qGraph = new CSPGraph();

        CSPVertex initialProcess = new CSPVertex("LoopProcess", true, true);
        initialProcess.setSeqCompositionVertex(true);

        CSPVertex qVertex = new CSPVertex("Q", true, true);

        CSPVertex skipProcess = new CSPVertex(SKIP);
        skipProcess.setSkipVertex(true);
        CSPVertex stopProcess = new CSPVertex(STOP);
        stopProcess.setStopVertex(true);

        baseGraph.addVertex(initialProcess);
        baseGraph.addVertex(stopProcess);
        baseGraph.addVertex(skipProcess);
        baseGraph.addVertex(qVertex);
        qGraph.addVertex(qVertex);

        RelationshipEdge q = qGraph.addEdge(qVertex, qVertex);
        q.setLabel("d -> e");

        RelationshipEdge e = baseGraph.addEdge(initialProcess, skipProcess);
        e.setLabel("a -> b");
        RelationshipEdge b = baseGraph.addEdge(initialProcess, qVertex);
        b.setLabel("c -> e");
        Graphs.addGraph(baseGraph, qGraph);

        return baseGraph;
    }

    public List<CSPGraph> getGraphs() {
        return graphs;
    }
}
