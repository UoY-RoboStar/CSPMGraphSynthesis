package org.ai4math.graphgenerator;

import org.ai4math.cspm.Keywords;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;

import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.ai4math.utils.GraphGenerationOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.io.IOException;
import java.util.regex.Pattern;

import static org.ai4math.testutils.Utils.typeOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GraphGeneratorTest {

    private static CSPGraph baseGraph;
    private static CSPGraph decoratedGraph;

    @BeforeAll
    static void initialise(){
        CSPGraph cspGraph = new CSPGraph();
        CSPGraph decGraph = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        CSPVertex x2 = new CSPVertex("x2", false, true);
        CSPVertex x3 = new CSPVertex("x3", false, true);

        cspGraph.addVertex(x1);
        cspGraph.addVertex(x2);
        cspGraph.addVertex(x3);
        decGraph.addVertex(x1);
        decGraph.addVertex(x2);
        decGraph.addVertex(x3);

        cspGraph.setInitialVertex(x1);
        decGraph.setInitialVertex(x1);

        RelationshipEdge e = cspGraph.addEdge(x1, x2);
        e.setLabel("label");
        cspGraph.addEdge(x2, x3);
        cspGraph.addEdge(x3, x1);

        RelationshipEdge f = decGraph.addEdge(x1, x2);
        f.setLabel("label");
        decGraph.addEdge(x2, x3);
        decGraph.addEdge(x3, x1);
        RelationshipEdge g = decGraph.addEdge(x1, x2);
        g.setLabel("charlabel?'a'");
        RelationshipEdge h = decGraph.addEdge(x2, x3);
        h.setLabel("enumlabel$HUnwow");
        RelationshipEdge i = decGraph.addEdge(x3, x1);
        i.setLabel("intlabel!40");
        RelationshipEdge j = decGraph.addEdge(x3, x3);
        j.setLabel("boollabel!true");
        RelationshipEdge k = decGraph.addEdge(x3, x2);
        k.setLabel("boollabel2.false");
        baseGraph = cspGraph;
        decoratedGraph = decGraph;
    }

    @Test
    void givenCountOfOne_whenGenerateBaseGraphs_thenAValidGraphShouldBeDefined() throws IOException{
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.generateBaseGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();

        assertEquals(1, graphs.size(), "Number of graphs is not equal to 1");
        assertFalse(vertices.isEmpty(), "Graph has no vertices");
        List<CSPVertex> vertx = vertices.stream().toList();

        assertTrue(vertx.getFirst().isInitialVertex(), "First vertex is not initial");
    }

    @Test
    void givenCountOfTwo_whenGenerateBaseGraphs_thenValidGraphsShouldBeDefined() throws IOException{
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.generateBaseGraphs(2);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size(), "Number of graphs is not equal to 1");

        for (CSPGraph cspGraph : graphs) {
            Set<CSPVertex> vertices = cspGraph.vertexSet();

            assertFalse(vertices.isEmpty(), "Graph has no vertices");
            List<CSPVertex> vertx = vertices.stream().toList();

            assertTrue(vertx.getFirst().isInitialVertex(),
                    "First vertex with name: "+vertx.getFirst().getName()+" is not initial");
        }
    }

    @Test
    void givenCountGreaterThanTwo_whenGenerateBaseGraphs_thenValidGraphsShouldBeDefined() throws IOException{
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.generateBaseGraphs(5);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(5, graphs.size(), "Number of graphs is not equal to 1");

        for (CSPGraph cspGraph : graphs) {
            Set<CSPVertex> vertices = cspGraph.vertexSet();

            assertFalse(vertices.isEmpty(), "Graph has no vertices");
            List<CSPVertex> vertx = vertices.stream().toList();

            assertTrue(vertx.getFirst().isInitialVertex(),
                    "First vertex with name: "+vertx.getFirst().getName()+" is not initial");
        }
    }

    @Test
    void givenCountOfOne_whenGenerateBaseGraphsThenCombinedGraph_thenAValidGraphShouldBeDefined() throws IOException{
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.generateBaseGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();

        assertEquals(1, graphs.size(), "Number of graphs is not equal to 1");
        assertFalse(vertices.isEmpty(), "Graph has no vertices");
        List<CSPVertex> vertx = vertices.stream().toList();

        assertTrue(vertx.getFirst().isInitialVertex(), "First vertex is not initial");

        graphGenerator.combineGraphs(1);

        graphs = graphGenerator.getGraphs();
        graph = graphs.getFirst();
        vertices = graph.vertexSet();

        assertEquals(2, graphs.size(), "Number of graphs is not equal to 2");
        assertFalse(vertices.isEmpty(), "Graph has no vertices");
        vertx = vertices.stream().toList();

        assertTrue(vertx.getFirst().isInitialVertex(), "First vertex is not initial");
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToComposition_thenGraphGeneratedWithCompositionVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(0);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean seqComp = false;
        for (CSPVertex vertex: vertices){
            if (vertex.isSeqCompositionVertex()){
                seqComp = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(seqComp, "Sequential composition vertex was not found in graph: "+graph.toString());
     }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToInternalChoice_thenGraphGeneratedWithInternalChoiceVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(1);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean intChoice = false;
        for (CSPVertex vertex: vertices){
            if (vertex.isInternalChoice()){
                intChoice = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(intChoice, "Internal choice vertex was not found in graph: "+graph.toString());
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToExternalChoiceUnguarded_thenGraphGeneratedWithExternalChoiceVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(2);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        CSPVertex extVertex = null;
        for (CSPVertex vertex: vertices){
            if (vertex.isExternalChoice()){
                extVertex = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertNotNull(extVertex, "External choice vertex was not found in graph: "+graph.toString());
        for(RelationshipEdge edge : graph.edgesOf(extVertex)){
            assertFalse(edge.getLabel().contains("&"),
                    "Guards are present on the branches of the choice: "+edge.getLabel());
        }
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToExternalChoiceGuardedRandom_thenGraphGeneratedWithExternalChoiceVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(2);
        when(random.nextInt(7,9)).thenReturn(8);
        when(random.nextBoolean()).thenReturn(true);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 2);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);


        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        CSPVertex extVertex = null;
        for (CSPVertex vertex: vertices){
            if (vertex.isExternalChoice()){
                extVertex = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertNotNull(extVertex, "External choice vertex was not found in graph: "+graph.toString());
        for(RelationshipEdge edge : graph.outgoingEdgesOf(extVertex)){
            assertTrue(edge.getLabel().contains("&"),
                    "Guards are present on the branches of the choice: "+edge.getLabel());
        }
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToGeneralisedParallel_thenGraphGeneratedWithGeneralisedParallelVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(3);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean genPar = false;
        CSPVertex genVert = graph.getInitialVertex();
        for (CSPVertex vertex: vertices){
            if (vertex.isGeneralisedParallel()){
                genPar = true;
                genVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(genPar, "Generalised parallel vertex was not found in graph: "+graph.toString());
        assertNotNull(genVert.getAlphabet(), "Alphabet of gen parallel vertex is null");
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToAlphabetisedParallel_thenGraphGeneratedWithAlphabetisedParallelVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(4);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean alphPar = false;
        CSPVertex alphVert = graph.getInitialVertex();
        for (CSPVertex vertex: vertices){
            if (vertex.isAlphabetisedParallel()){
                alphPar = true;
                alphVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(alphPar, "Alphabetised parallel vertex was not found in graph: "+graph.toString());
        assertNotNull(alphVert.getAlphabet(), "Alphabet of alph parallel vertex is null");
    }


    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToInterleave_thenGraphGeneratedWithInterleaveVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(5);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean inter = false;
        for (CSPVertex vertex: vertices){
            if (vertex.isInterleave()){
                inter = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(inter, "Interleave vertex was not found in graph: "+graph.toString());
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToInterrupt_thenGraphGeneratedWithInterruptVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(6);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 2);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean inter = false;
        for (CSPVertex vertex: vertices){
            if (vertex.isInterrupt()){
                inter = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(inter, "Interrupt vertex was not found in graph: "+graph.toString());
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToException_thenGraphGeneratedWithExceptionVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(7);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 2);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean excep = false;
        CSPVertex excepVert = graph.getInitialVertex();
        for (CSPVertex vertex: vertices){
            if (vertex.isException()){
                excep = true;
                excepVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(excep, "Exception vertex was not found in graph: "+graph.toString());
        assertNotNull(excepVert.getAlphabet(), "Alphabet of exception vertex is null");
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToTimeout_thenGraphGeneratedWithTimeoutVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(8);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 2);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean time = false;
        for (CSPVertex vertex: vertices){
            if (vertex.isTimeout()){
                time = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(time, "Timeout vertex was not found in graph: "+graph.toString());
    }


    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToRepOpExtChoice_thenGraphGeneratedWithRepOpExtChoiceVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(9);
        when(random.nextInt(0, 5)).thenReturn(3);
        //when(random.nextInt(0,4)).thenReturn(1);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 2);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        CSPVertex repVert = null;
        CSPVertex.RepOp repOp = null;
        for (CSPVertex vertex: vertices){
            if (vertex.getReplicatedOperator()!=null){
                repOp = vertex.getReplicatedOperator();
                repVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertNotNull(repVert, "Graph is missing a replicated operator: "+graph.toString());
        assertNotNull(repVert.getRepOpType(), "Type of replicated operator vertex is null");
        assertNotNull(repOp, "Replicated Operator not found within graph: "+graph.toString());
        assertEquals(CSPVertex.RepOp.ExtChoice, repOp,
                "Replicated Operator variant within the graph is unexpected: "+graph.toString());

        Set<RelationshipEdge> outgoing = graph.outgoingEdgesOf(repVert);
        CSPVertex target = null;
        for (RelationshipEdge edge : outgoing){
            if (edge.getLabel().equals(Keywords.LAMBDA)){
                target = graph.getEdgeTarget(edge);
            }
        }

        assertNotNull(target, "No transition found for rep operator");
        assertNotNull(target.getParameter(), "No parameter defined for vertex: "+target.toString());
        assertEquals(Keywords.INT, target.getParameter().getValue(), "Type of parameter is not expected");
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToRepOpAlphPar_thenGraphGeneratedWithRepOpAlphParVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(9);
        //when(random.nextInt(0,4)).thenReturn(1);
        when(random.nextInt(0,5)).thenReturn(1);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 2);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        CSPVertex repVert = null;
        CSPVertex.RepOp repOp = null;
        for (CSPVertex vertex: vertices){
            if (vertex.getReplicatedOperator()!=null){
                repOp = vertex.getReplicatedOperator();
                repVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertNotNull(repOp, "Replicated Operator not found within graph: "+graph.toString());
        assertEquals(CSPVertex.RepOp.AlphParallel, repOp,
                "Replicated Operator variant within the graph is unexpected: "+graph.toString());
        assertNotNull(repVert.getAlphabet(), "Alphabet is not populated for: "+repVert.toString());

        Set<RelationshipEdge> outgoing = graph.outgoingEdgesOf(repVert);
        CSPVertex target = null;
        for (RelationshipEdge edge : outgoing){
            if (edge.getLabel().equals(Keywords.LAMBDA)){
                target = graph.getEdgeTarget(edge);
            }
        }

        assertNotNull(target, "No transition found for rep operator");
        assertNotNull(target.getParameter(), "No parameter defined for vertex: "+target.toString());
        assertEquals(Keywords.INT, target.getParameter().getValue(), "Type of parameter is not expected");
    }

    @Test
    void givenOneBaseGraph_whenGenerateBaseGraphWithHidden_thenGraphGeneratedWithHiddenVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(5);
        when(random.nextInt(0,30)).thenReturn(7);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean hidden = false;
        for (CSPVertex vertex: vertices){
            if (!vertex.getHidden().isEmpty()){
                hidden = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(hidden, "A hidden set was not found in graph: "+graph.toString());
    }

    @Test
    void givenOneBaseGraph_whenGenerateBaseGraphWithRenaming_thenGraphGeneratedWithRenamingVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(5);
        when(random.nextInt(0,30)).thenReturn(18);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, true, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        CSPVertex renamingVert = null;
        for (CSPVertex vertex: vertices){
            if (!vertex.getRenaming().isEmpty()){
                renamingVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertNotNull(renamingVert, "Renaming was not found in graph: "+graph.toString());
        Pattern pattern = Pattern.compile("[a-zA-Z]*[!?$][a-zA-Z0-9]*]");
        for (String renamingChannel : renamingVert.getRenaming().keySet()) {
            assertFalse(pattern.matcher(renamingChannel).find());
            assertFalse(pattern.matcher(renamingVert.getRenaming().get(renamingChannel)).find());
            assertEquals(typeOf(renamingChannel), typeOf(renamingVert.getRenaming().get(renamingChannel)),
                    "Types of renamings are not equal");
        }
    }

    @Test
    void givenOneBaseGraphForceDecorated_whenGenerateBaseGraphWithRenaming_thenGraphGeneratedWithRenamingVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,19)).thenReturn(5);
        when(random.nextInt(0,30)).thenReturn(18);
        when(random.nextInt(1,23)).thenReturn(23);
        when(random.nextInt(0,11)).thenReturn(5);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, true, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(decoratedGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(decoratedGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        CSPVertex renamingVert = null;
        for (CSPVertex vertex: vertices){
            if (!vertex.getRenaming().isEmpty()){
                renamingVert = vertex;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertNotNull(renamingVert, "Renaming was not found in graph: "+graph.toString());
        Pattern pattern = Pattern.compile("[a-zA-Z]*[!?$][a-zA-Z0-9]*]");
        for (String renamingChannel : renamingVert.getRenaming().keySet()) {
            System.out.println(renamingChannel);
            assertFalse(pattern.matcher(renamingChannel).find());
            System.out.println(renamingVert.getRenaming().get(renamingChannel));
            assertFalse(pattern.matcher(renamingVert.getRenaming().get(renamingChannel)).find());
            assertEquals(typeOf(renamingChannel), typeOf(renamingVert.getRenaming().get(renamingChannel)),
                    "Types of renamings are not equal");
        }
    }

    @Test
    void givenOneBaseGraph_whenGenerateBaseGraphWithRenamingFlagFalse_thenGraphGeneratedWithoutRenamingVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(5);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean renam = false;
        for (CSPVertex vertex: vertices){
            if (!vertex.getRenaming().isEmpty()){
                renam = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertFalse(renam, "Renaming was found in graph: "+graph.toString());
    }


    @Test
    void givenNonEmptyListOfDecoratedStringsForNonAlphabet_whenRandomSubList_thenListOfDecoratedMessagesReturned(){
        Random random = spy(Random.class);
        when(random.nextInt(1,5)).thenReturn(3);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);
        List<String> messages = new ArrayList<>(List.of("random!true", "list?false", "of.4", "possible", "strings"));
        Map<String, List<String>> decorations = new HashMap<>();
        decorations.put("random", List.of("!", "true"));
        decorations.put("list", List.of("?", "false"));
        decorations.put("of", List.of(".", "4"));
        decorations.put("possible", List.of());
        decorations.put("strings", List.of());

        List<String> sublist = graphGenerator.randomSubList(messages, false);
        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(3, sublist.size());

        for (String message: sublist){
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                String channel = comps[0];
                assertEquals(decorations.get(channel).getFirst(), comps[1]);
                assertEquals(decorations.get(channel).get(1), comps[2]);
            }
        }
    }

    @Test
    void givenEmptyListOfStringsForNonAlphabet_whenRandomSubList_thenEmptyListOfMessagesIsReturned(){
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        List<String> messages = new ArrayList<>();

        List<String> sublist = graphGenerator.randomSubList(messages, false);

        assertEquals(messages, sublist,"Nonempty list of messages returned: "+ sublist);
    }

    @Test
    void givenNonEmptyListOfDecoratedStringsForAlphabet_whenRandomSubList_thenListOfDottedMessagesReturned(){
        Random random = spy(Random.class);
        when(random.nextInt(1,5)).thenReturn(3);

        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        graphGenerator.setRandom(random);
        List<String> messages = new ArrayList<>(List.of("random!true", "list?false", "of.4", "possible", "strings"));
        Map<String, List<String>> decorations = new HashMap<>();
        decorations.put("random", List.of("!", "true"));
        decorations.put("list", List.of("?", "false"));
        decorations.put("of", List.of(".", "4"));
        decorations.put("possible", List.of());
        decorations.put("strings", List.of());

        List<String> sublist = graphGenerator.randomSubList(messages, true);
        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(3, sublist.size());

        for (String message: sublist){
            assertFalse(message.contains("!"), "Message "+message+" contains an unexpected decoration");
            assertFalse(message.contains("?"), "Message "+message+" contains an unexpected decoration");
            assertFalse(message.contains("$"), "Message "+message+" contains an unexpected decoration");
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                assertEquals(".", comps[1]);
           }
        }
    }

    @Test
    void givenEmptyListOfStringsForAlphabet_whenRandomSubList_thenEmptyListIsReturned(){
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        List<String> messages = new ArrayList<>();

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertEquals(messages, sublist,"Nonempty list of messages returned: "+ sublist);
    }

    @Test
    void givenSingletonListOfDecoratedStringForAlphabet_whenRandomSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        List<String> messages = new ArrayList<>(List.of("strings?4"));

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        assertFalse(sublist.getFirst().contains("!"), "Message "+sublist.getFirst()+" contains an unexpected decoration");
        assertFalse(sublist.getFirst().contains("?"), "Message "+sublist.getFirst()+" contains an unexpected decoration");
        assertFalse(sublist.getFirst().contains("$"), "Message "+sublist.getFirst()+" contains an unexpected decoration");
        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);
        if (comps.length>1){
            assertEquals(".", comps[1]);
        }
    }

    @Test
    void givenSingletonListOfUndecoratedStringForAlphabet_whenRandomSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        List<String> messages = new ArrayList<>(List.of("strings"));

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);

        assertFalse(comps.length>1, "Messages containing decoration in list: "+ sublist);
    }

    @Test
    void givenEmptyStringsListForAlphabet_whenRandomSubList_thenListIsReturned(){
        GraphGenerationOptions ggo = new GraphGenerationOptions(true, false, 1);
        GraphGenerator graphGenerator = new GraphGenerator(ggo);
        List<String> messages = new ArrayList<>(List.of("", ""));

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertTrue(sublist.isEmpty(), "Messages list is not empty");
    }

    @Test
    void givenNonEmptyListOfDecoratedStringsForNonAlphabet_whenRandomSetSizeSubList_thenListOfDecoratedMessagesReturned(){

        List<String> messages = new ArrayList<>(List.of("random!true", "list?false", "of.4", "possible", "strings"));
        Map<String, List<String>> decorations = new HashMap<>();
        decorations.put("random", List.of("!", "true"));
        decorations.put("list", List.of("?", "false"));
        decorations.put("of", List.of(".", "4"));
        decorations.put("possible", List.of());
        decorations.put("strings", List.of());

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 3,false);
        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(3, sublist.size());

        for (String message: sublist){
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                String channel = comps[0];
                assertEquals(decorations.get(channel).getFirst(), comps[1]);
                assertEquals(decorations.get(channel).get(1), comps[2]);
            }
        }
    }

    @Test
    void givenEmptyListOfStringsForNonAlphabet_whenRandomSetSizeSubList_thenEmptyListOfMessagesIsReturned(){
        List<String> messages = new ArrayList<>();

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 2,false);

        assertEquals(messages, sublist,"Nonempty list of messages returned: "+ sublist);
    }

    @Test
    void givenSingletonListOfUndecoratedStringForNonAlphabet_whenRandomSetSizeSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        List<String> messages = new ArrayList<>(List.of("strings"));

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 1, false);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);

        assertFalse(comps.length>1, "Messages containing decoration in list: "+ sublist);
    }

    @Test
    void givenNonEmptyListOfDecoratedStringsForAlphabet_whenRandomSetSizeSubList_thenListOfUndecoratedMessagesReturned(){
        List<String> messages = new ArrayList<>(List.of("random!true", "list?false", "of.4", "possible", "strings"));
        Map<String, List<String>> decorations = new HashMap<>();
        decorations.put("random", List.of("!", "true"));
        decorations.put("list", List.of("?", "false"));
        decorations.put("of", List.of(".", "4"));
        decorations.put("possible", List.of());
        decorations.put("strings", List.of());

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 6,true);
        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(5, sublist.size());

        for (String message: sublist){
            assertFalse(message.contains("!"), "Message "+message+" contains an unexpected decoration");
            assertFalse(message.contains("?"), "Message "+message+" contains an unexpected decoration");
            assertFalse(message.contains("$"), "Message "+message+" contains an unexpected decoration");
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                assertEquals(".", comps[1]);
            }
        }
    }

    @Test
    void givenEmptyListOfStringsForAlphabet_whenRandomSetSizeSubList_thenEmptyListIsReturned(){
        List<String> messages = new ArrayList<>();

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 3,true);

        assertEquals(messages, sublist,"Nonempty list of messages returned: "+ sublist);
    }

    @Test
    void givenSingletonListOfDecoratedStringForAlphabet_whenRandomSetSizeSubList_thenListOfSingleDottedMessageIsReturned(){
        List<String> messages = new ArrayList<>(List.of("strings?4"));

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 2,true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        assertFalse(sublist.getFirst().contains("!"), "Message "+sublist.getFirst()+" contains an unexpected decoration");
        assertFalse(sublist.getFirst().contains("?"), "Message "+sublist.getFirst()+" contains an unexpected decoration");
        assertFalse(sublist.getFirst().contains("$"), "Message "+sublist.getFirst()+" contains an unexpected decoration");
        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);
        if (comps.length>1){
            assertEquals(".", comps[1]);
        }
    }

    @Test
    void givenSingletonListOfUndecoratedStringForAlphabet_whenRandomSetSizeSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        List<String> messages = new ArrayList<>(List.of("strings"));

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 1, true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);

        assertFalse(comps.length>1, "Messages containing decoration in list: "+ sublist);
    }

    @Test
    void givenEmptyStringsListForAlphabet_whenRandomSetSizeSubList_thenListIsReturned(){
        List<String> messages = new ArrayList<>(List.of("", ""));

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 1, true);

        assertTrue(sublist.isEmpty(), "Messages list is not empty");
    }


    @Test
    void givenListWithEmptyStringsForAlphabet_whenRandomSetSizeSubList_thenListIsReturned(){
        List<String> messages = new ArrayList<>(List.of("random!true", "list?false", "", "possible", "strings"));
        Map<String, List<String>> decorations = new HashMap<>();
        decorations.put("random", List.of("!", "true"));
        decorations.put("list", List.of("?", "false"));
        decorations.put("possible", List.of());
        decorations.put("strings", List.of());

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 6,true);
        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(4, sublist.size());

        for (String message: sublist){
            assertFalse(message.contains("!"), "Message "+message+" contains an unexpected decoration");
            assertFalse(message.contains("?"), "Message "+message+" contains an unexpected decoration");
            assertFalse(message.contains("$"), "Message "+message+" contains an unexpected decoration");
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                assertEquals(".", comps[1]);
            }
        }
    }
}
