package org.ai4math.graphgenerator;


import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;
import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.jgrapht.Graphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.ai4math.cspm.Keywords.*;

public class GraphGenerator {
    public List<CSPGraph> graphs;

    public List<CSPGraph> GenerateGraphSet() {
        // take parameters for generation and record those parameters
        // side note: make a tool to parse the csp files to get data about representation in the dataset
        //              versus representation in existing examples


        List<CSPGraph> graphs = new ArrayList<>();
        //graphs.add(BasicGraph());
        //graphs.add(CompositionalGraph());
        //graphs.add(ParallelGraph());
        graphs.add(LoopGraph());

        // Add graph combination step

        return graphs;
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
