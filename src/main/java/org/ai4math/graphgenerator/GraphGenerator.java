package org.ai4math.graphgenerator;


import org.ai4math.cspm.Keywords;
import org.ai4math.graphgenerator.utils.*;
import org.jgrapht.Graphs;

import java.util.*;

import static java.util.Map.entry;
import static org.ai4math.cspm.Keywords.*;
import static org.ai4math.graphgenerator.utils.NameGenerator.generateMessages;
import static org.ai4math.graphgenerator.utils.NameGenerator.generateProcessName;

public class GraphGenerator {
    public List<CSPGraph> graphs;
    public NameVerifier nameVerifier;
    public Random random;
    public Boolean decorated;
    public Boolean renaming;

    public GraphGenerator(boolean decorated, boolean renaming){
        this.graphs = List.of();
        this.nameVerifier = new NameVerifier();
        this.random = new Random();
        this.decorated = decorated;
        this.renaming = renaming;
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

            List<String> messages = generateMessages(this.random.nextInt(30), this.nameVerifier, this.decorated);
            String label = generateEdge(messages);

            CSPVertex initialProcess = new CSPVertex(processName, true, true);
            baseGraph.addVertex(initialProcess);

            CSPVertex process = generateProcess(messages, false, false);
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

        CSPVertex newProcess = generateProcess(messages, false, limit == 1);
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

    private CSPVertex generateProcess(List<String> messages, boolean nonTerminal, boolean terminate){
        CSPVertex process = new CSPVertex("temp");
        int choice = this.random.nextInt(nonTerminal?1:0,terminate?2:3);
        if (choice == 0) {
            process.setName(STOP);
            process.setStopVertex(true);
        } else if (choice == 1) {
            process.setName(SKIP);
            process.setStopVertex(true);
        } else if (choice == 2) {
            process = generateNonTerminalProcess();
        }

        int hidden = this.random.nextInt(0,30);
        if (hidden == 7) {
            Set<String> hiddenChannels = new HashSet<>(randomSubList(messages, true));
            process.setHidden(hiddenChannels);
        }
        if (renaming) {
            int renamingInt = this.random.nextInt(0, 30);
            if (renamingInt == 18) {
                List<String> renameChannels = randomSubList(messages, true);
                List<String> renamedChannels = randomSetSizeSubList(messages, renameChannels.size(), true);
                Map<String, String> renamings = new LinkedHashMap<>();
                for (int i = 0; i < renameChannels.size() - 1; i++) {
                    renamings.put(renameChannels.get(i), renamedChannels.get(i));
                }
                process.setRenaming(renamings);
            }
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

        CSPVertex newProcess = generateProcess(messages, true, false);
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

        int choice = this.random.nextInt(2);
        if (choice == 0) {
            CSPVertex newProcess = generateProcess(messages, false, limit == 1);
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

    private CSPVertex getRandomVertex(CSPGraph graph){
        CSPVertex vertex = graph.vertexSet().stream().toList()
                .get(this.random.nextInt(graph.vertexSet().size()));
        while (vertex.isInterleave() || vertex.isAlphabetisedParallel() || vertex.isGeneralisedParallel() ||
                vertex.isInternalChoice() || vertex.isExternalChoice() || vertex.isSeqCompositionVertex()){
            vertex = graph.vertexSet().stream().toList()
                    .get(this.random.nextInt(graph.vertexSet().size()));
        }

        return vertex;
    }

    private void generateCombinedGraph(CSPGraph sourceGraph, CSPGraph graph) {
        List<String> messages = generateMessages(this.random.nextInt(30), this.nameVerifier, this.decorated);
        messages.addAll(getMessagesFromGraph(sourceGraph));

        CSPVertex vertex = getRandomVertex(graph.vertexSet().isEmpty()? sourceGraph:graph);
        graph.addVertex(vertex);

        CSPVertex combinationProcess = generateCombinationProcess(messages);
        graph.addVertex(combinationProcess);

        if (combinationProcess.isSeqCompositionVertex()) {
                if (this.random.nextBoolean()) {
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
                if (this.random.nextBoolean()) {
                    generateStarterGraph(messages, combinationProcess, graph, false);
                }
                RelationshipEdge e = graph.addEdge(combinationProcess, vertex);
                e.setLabel(generateEdge(messages));
                CSPGraph extraGraph = generateGraph(combinationProcess, messages, 4);
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
    }

    private CSPVertex generateCombinationProcess(List<String> messages){
        CSPVertex process = new CSPVertex("temp");
        int choice = this.random.nextInt(0,6);
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
            Set<String> alphabetA = new HashSet<>(randomSubList(messages, true));
            List<Set<String>> alphabet = List.of(alphabetA);
            process.setAlphabet(alphabet);
        } else if (choice == 4) {
            // alphabetised parallel
            process.setName(generateProcessName(this.nameVerifier));
            process.setProcessVertex(true);
            process.setAlphabetisedParallel(true);
            Set<String> alphabetA = new HashSet<>(randomSubList(messages, true));
            Set<String> alphabetB = new HashSet<>(randomSubList(messages, true));
            List<Set<String>> alphabet = List.of(alphabetA, alphabetB);
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
        List<String> messagesTrimmed = new ArrayList<>(messages);
        messagesTrimmed.removeIf(n-> Objects.equals(n, "")); // remove empty strings from this
        if (!messagesTrimmed.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.join(" -> ", randomSubList(messagesTrimmed, false)));
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
                    if (component.matches("[a-zA-Z]*([!?.][a-zA-Z0-9]*)?")) {
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
            return alphabet?List.of(getUndecorated(list.getFirst())):list;
        } else if (list.isEmpty()){
            return list;
        }

        int newSize = this.random.nextInt(1, list.size());
        return getStrings(list, newSize, alphabet);
    }

    public static List<String> randomSetSizeSubList(List<String> list, int size, boolean alphabet) {
        list.removeIf(n->Objects.equals(n,""));
        if (list.size()==1){
            return alphabet?List.of(getUndecorated(list.getFirst())):list;
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
                sublistCopy.add(getUndecorated(item));
            }
            sublist = sublistCopy;
        }
        return sublist;
    }

    private static String getUndecorated(String item){
        String[] items = item.split("[!?$\\.]");
        StringBuilder sb = new StringBuilder();
        sb.append(items[0]);
        if (items.length>1) {
            sb.append(".").append(Keywords.TYPE_PLACEHOLDER);
        }
        return sb.toString();
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
