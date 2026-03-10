package org.ai4math.graphgenerator;


import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;
import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.apache.commons.lang3.RandomStringUtils;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.*;
import java.util.stream.Collectors;

import static org.ai4math.cspm.Keywords.*;

public class GraphGenerator {
    public List<CSPGraph> graphs;

    public GraphGenerator(){
        this.graphs = List.of();
    }

    public List<CSPGraph> GenerateGraphSet() {
        // take parameters for generation and record those parameters
        // side note: make a tool to parse the csp files to get data about representation in the dataset
        //              versus representation in existing examples

        List<CSPGraph> graphs = new ArrayList<>();
        /*graphs.add(BasicGraph());
        graphs.add(CompositionalGraph());
        graphs.add(ParallelGraph());
        graphs.add(LoopGraph());*/

        GenerateBaseGraphs(10);
        //CombineGraphs(10);

        // Add graph combination step

        return this.graphs;
    }

    public void GenerateBaseGraphs(int count){
        List<CSPGraph> baseGraphs = new ArrayList<>(this.graphs);
        int i = 0;
        Random r = new Random();

        while (i<count){
            CSPGraph baseGraph = new CSPGraph();

            int length = r.nextInt(1,12);
            String processName = RandomStringUtils.random(length, true, false);

            List<String> messages = GenerateMessages(r.nextInt(30));
            String label = GenerateEdge(messages);

            CSPVertex initialProcess = new CSPVertex(processName, true, true);
            baseGraph.addVertex(initialProcess);

            CSPVertex process = GenerateProcess();
            baseGraph.addVertex(process);
            RelationshipEdge e = baseGraph.addEdge(initialProcess, process);
            e.setLabel(label);

            if (!process.getName().equals(SKIP) && !process.getName().equals(STOP)){
                CSPGraph newGraph = GenerateGraph(process, messages, 15);
                if (!newGraph.vertexSet().isEmpty()) {
                    Graphs.addGraph(baseGraph, newGraph);
                }
            }
            baseGraphs.add(baseGraph);
            i++;
        }

        this.graphs = baseGraphs;
    }

    private CSPGraph GenerateGraph(CSPVertex process, List<String> messages, int limit){
        CSPGraph graph = new CSPGraph();

        if (limit == 0){
            return graph;
        }

        String label = GenerateEdge(messages);

        process.setInitialVertex(true);
        graph.addVertex(process);

        CSPVertex newProcess = GenerateProcess();
        graph.addVertex(newProcess);
        RelationshipEdge e = graph.addEdge(process, newProcess);
        e.setLabel(label);

        if (!newProcess.getName().equals(SKIP) && !newProcess.getName().equals(STOP)){
            CSPGraph newGraph = GenerateGraph(newProcess, messages, limit - 1);
            if (!newGraph.vertexSet().isEmpty()) {
                Graphs.addGraph(graph, newGraph);
            }
        }

        return graph;
    }

    private CSPVertex GenerateProcess(){
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
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
        }

        return process;
    }


    public void CombineGraphs(int count){
        List<CSPGraph> baseGraphs = new ArrayList<>(this.graphs);
        List<CSPGraph> combinedGraphs = new ArrayList<>();
        int i = 0;
        Random r = new Random();

        while (i<count){
            CSPGraph sourceGraph = baseGraphs.get(r.nextInt(baseGraphs.size()));
            CSPGraph graph = new CSPGraph();

            List<String> messages = GenerateMessages(r.nextInt(30));
            messages.addAll(GetMessagesFromGraph(sourceGraph));

            CSPVertex vertex = sourceGraph.vertexSet().stream().collect(Collectors.toList())
                    .get(r.nextInt(sourceGraph.vertexSet().size()));
            graph.addVertex(vertex);

            CSPVertex newProcess = GenerateCombinationProcess(messages);
            graph.addVertex(newProcess);

            if (r.nextBoolean()){
                CSPVertex newInitProcess = new CSPVertex(RandomStringUtils.randomAlphabetic(1, 12),true, true);
                CSPGraph newGraph = GenerateGraph(newInitProcess, messages, 4);
                // todo: need a new function that doesn't end in SKIP or STOP  and returns the final vertex
                if (!newGraph.vertexSet().isEmpty()) {
                    Graphs.addGraph(graph, newGraph);
                }
                RelationshipEdge e = graph.addEdge(newInitProcess, newProcess);
                String label = GenerateEdge(messages);
                e.setLabel(label);
            }

            String label = GenerateEdge(messages);

            if (newProcess.isSeqCompositionVertex()) {
                if (r.nextBoolean()) {
                    // add tick transition from source to seqcomp, then add new graph from seqcomp
                    RelationshipEdge e = graph.addEdge(vertex, newProcess);
                    e.setLabel(TICK);
                    GenerateGraph(newProcess, messages, 4);
                } else {
                    // add new graph, then add tick transition to seqcomp, then add sourcegraph
                    CSPVertex newInitProcess = new CSPVertex(RandomStringUtils.randomAlphabetic(1, 12), true, true);
                    CSPGraph newGraph = GenerateGraph(newInitProcess, messages, 4);
                    // todo: need a new function that doesn't end in SKIP or STOP  and returns the final vertex                      if (!newGraph.vertexSet().isEmpty()) {
                    if (!newGraph.vertexSet().isEmpty()) {

                        Graphs.addGraph(graph, newGraph);
                    }
                    RelationshipEdge e = graph.addEdge(newInitProcess, newProcess);
                    e.setLabel(TICK);
                    RelationshipEdge f = graph.addEdge(newProcess, vertex);
                    f.setLabel(label);
                }
            } else if (newProcess.isInternalChoice() || newProcess.isExternalChoice() || newProcess.isInterleave()
                    || newProcess.isAlphabetisedParallel() || newProcess.isGeneralisedParallel()) {
                // need at least two processes connected to the newProcess, either with or without messages
                // could add guards on these also
                if (r.nextBoolean()) {
                    RelationshipEdge e = graph.addEdge(newProcess, vertex);
                    e.setLabel(label);
                    CSPGraph extraGraph = GenerateGraph(newProcess, messages, 4);
                    CSPVertex extraVertex = extraGraph.getInitialVertex();
                    if (!extraGraph.vertexSet().isEmpty()) {
                        Graphs.addGraph(graph, extraGraph);
                        RelationshipEdge f = graph.addEdge(newProcess, extraVertex);

                        label = GenerateEdge(messages);
                        f.setLabel(label);
                    }
                } else {
                    RelationshipEdge e = graph.addEdge(newProcess, vertex);
                    CSPGraph extraGraph = GenerateGraph(newProcess, messages, 4);
                    CSPVertex extraVertex = extraGraph.getInitialVertex();
                    if (!extraGraph.vertexSet().isEmpty()) {
                        Graphs.addGraph(graph, extraGraph);
                        RelationshipEdge f = graph.addEdge(newProcess, extraVertex);
                    }
                }
            }

            i++;
        }

        baseGraphs.addAll(combinedGraphs);
        this.graphs = baseGraphs;
    }

    private CSPVertex GenerateCombinationProcess(List<String> messages){
        Random r = new Random();

        CSPVertex process = new CSPVertex("temp");
        int choice = r.nextInt(0,6);
        if (choice == 0) {
            // sequential composition
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
            process.setSeqCompositionVertex(true);
        } else if (choice == 1) {
            // internal choice
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
            process.setInternalChoice(true);
        } else if (choice == 2) {
            // external choice
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
            process.setExternalChoice(true);
        } else if (choice == 3) {
            // generalised parallel
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
            process.setGeneralisedParallel(true);
            Set<String> alphA = new HashSet<>(randomSubList(messages));
            List<Set<String>> alphabet = List.of(alphA);
            process.setAlphabet(alphabet);
        } else if (choice == 4) {
            // alphabetised parallel
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
            process.setAlphabetisedParallel(true);
            Set<String> alphA = new HashSet<>(randomSubList(messages));
            Set<String> alphB = new HashSet<>(randomSubList(messages));
            List<Set<String>> alphabet = List.of(alphA, alphB);
            process.setAlphabet(alphabet);
        } else if (choice == 5) {
            // interleave
            process.setName(RandomStringUtils.randomAlphabetic(1, 12));
            process.setProcessVertex(true);
            process.setInterleave(true);
        }

        return process;
    }

    private String GenerateEdge(List<String> messages){
        if (messages.size()>0) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.join(" -> ", randomSubList(messages)));
            return sb.toString();
        }
        return "";
    }

    private List<String> GenerateMessages(int count){
        List<String> messages = new ArrayList<>();
        int i = 0;
        Random r = new Random();

        while (i<count) {
            int length = r.nextInt(1, 25);
            String message = RandomStringUtils.random(length, true, false);
            if (!message.isEmpty()) {
                messages.add(message);
            }
            i++;
        }

        return messages;
    }

    private List<String> GetMessagesFromGraph(CSPGraph graph){
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
        Random r = new Random();
        int newSize = r.nextInt(1, list.size());
        list = new ArrayList<>(list);
        Collections.shuffle(list);
        return list.subList(0, newSize);
    }

    public CSPGraph BasicGraph() {
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

    public CSPGraph CompositionalGraph() {
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

    public CSPGraph ParallelGraph() {
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

    public CSPGraph LoopGraph() {
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
