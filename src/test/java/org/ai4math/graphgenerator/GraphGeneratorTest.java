package org.ai4math.graphgenerator;

import org.ai4math.cspm.Keywords;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;

import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class GraphGeneratorTest {

    private static CSPGraph baseGraph;

    @BeforeAll
    static void initialise(){
        CSPGraph cspGraph = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        CSPVertex x2 = new CSPVertex("x2", false, true);
        CSPVertex x3 = new CSPVertex("x3", false, true);

        cspGraph.addVertex(x1);
        cspGraph.addVertex(x2);
        cspGraph.addVertex(x3);

        cspGraph.setInitialVertex(x1);

        RelationshipEdge e = cspGraph.addEdge(x1, x2);
        e.setLabel("label");
        cspGraph.addEdge(x2, x3);
        cspGraph.addEdge(x3, x1);
        baseGraph = cspGraph;
    }

    @Test
    void givenCountOfOne_whenGenerateBaseGraphs_thenAValidGraphShouldBeDefined() throws IOException{
        GraphGenerator graphGenerator = new GraphGenerator();
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
        GraphGenerator graphGenerator = new GraphGenerator();
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
        GraphGenerator graphGenerator = new GraphGenerator();
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
        GraphGenerator graphGenerator = new GraphGenerator();
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

        GraphGenerator graphGenerator = new GraphGenerator();
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

        GraphGenerator graphGenerator = new GraphGenerator();
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
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToExternalChoice_thenGraphGeneratedWithExternalChoiceVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(2);

        GraphGenerator graphGenerator = new GraphGenerator();
        graphGenerator.setRandom(random);

        graphGenerator.addGraph(baseGraph);
        graphGenerator.combineGraphs(1);

        List<CSPGraph> graphs = graphGenerator.getGraphs();
        assertEquals(2, graphs.size());
        graphs.remove(baseGraph);

        CSPGraph graph = graphs.getFirst();
        Set<CSPVertex> vertices = graph.vertexSet();
        boolean extChoice = false;
        for (CSPVertex vertex: vertices){
            if (vertex.isExternalChoice()){
                extChoice = true;
            }
            if (graph.edgesOf(vertex).isEmpty()){
                assertTrue(vertex.isStopVertex() || vertex.isSkipVertex());
            }
        }

        assertTrue(extChoice, "Esternal choice vertex was not found in graph: "+graph.toString());
    }

    @Test
    void givenOneBaseGraph_whenGenerateCombinedGraphSetToGeneralisedParallel_thenGraphGeneratedWithGeneralisedParallelVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(3);

        GraphGenerator graphGenerator = new GraphGenerator();
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

        GraphGenerator graphGenerator = new GraphGenerator();
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

        GraphGenerator graphGenerator = new GraphGenerator();
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
    void givenOneBaseGraph_whenGenerateBaseGraphWithHidden_thenGraphGeneratedWithHiddenVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(5);

        GraphGenerator graphGenerator = new GraphGenerator();
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
    void givenOneBaseGraph_whenGenerateBaseGraphWithRenaming_thenGraphGeneratedWithRenamingVertex(){
        Random random = spy(Random.class);
        when(random.nextInt(0,6)).thenReturn(5);

        GraphGenerator graphGenerator = new GraphGenerator();
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
    void givenNonEmptyListOfDecoratedStringsForNonAlphabet_whenRandomSubList_thenListOfDecoratedMessagesReturned(){
        Random random = spy(Random.class);
        when(random.nextInt(1,5)).thenReturn(3);

        GraphGenerator graphGenerator = new GraphGenerator();
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
        GraphGenerator graphGenerator = new GraphGenerator();
        List<String> messages = new ArrayList<>();

        List<String> sublist = graphGenerator.randomSubList(messages, false);

        assertEquals(messages, sublist,"Nonempty list of messages returned: "+ sublist);
    }

    @Test
    void givenNonEmptyListOfDecoratedStringsForAlphabet_whenRandomSubList_thenListOfUndecoratedMessagesReturned(){
        Random random = spy(Random.class);
        when(random.nextInt(1,5)).thenReturn(3);

        GraphGenerator graphGenerator = new GraphGenerator();
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
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                assertTrue(decorations.containsKey(comps[0]), "Random message generated"+comps[0]);
                assertEquals(".", comps[1]);
                assertEquals(Keywords.TYPE_PLACEHOLDER, comps[2]);
            }
        }
    }

    @Test
    void givenEmptyListOfStringsForAlphabet_whenRandomSubList_thenEmptyListIsReturned(){
        GraphGenerator graphGenerator = new GraphGenerator();
        List<String> messages = new ArrayList<>();

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertEquals(messages, sublist,"Nonempty list of messages returned: "+ sublist);
    }

    @Test
    void givenSingletonListOfDecoratedStringForAlphabet_whenRandomSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        GraphGenerator graphGenerator = new GraphGenerator();
        List<String> messages = new ArrayList<>(List.of("strings?4"));

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);
        assertEquals(".", comps[1]);
        assertEquals(Keywords.TYPE_PLACEHOLDER, comps[2]);
    }

    @Test
    void givenSingletonListOfUndecoratedStringForAlphabet_whenRandomSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        GraphGenerator graphGenerator = new GraphGenerator();
        List<String> messages = new ArrayList<>(List.of("strings"));

        List<String> sublist = graphGenerator.randomSubList(messages, true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);

        assertFalse(comps.length>1, "Messages containing decoration in list: "+ sublist);
    }

    @Test
    void givenEmptyStringsListForAlphabet_whenRandomSubList_thenListIsReturned(){
        GraphGenerator graphGenerator = new GraphGenerator();
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
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                assertTrue(decorations.containsKey(comps[0]), "Random message generated"+comps[0]);
                assertEquals(".", comps[1]);
                assertEquals(Keywords.TYPE_PLACEHOLDER, comps[2]);
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
    void givenSingletonListOfDecoratedStringForAlphabet_whenRandomSetSizeSubList_thenListOfSingleUndecoratedMessageIsReturned(){
        List<String> messages = new ArrayList<>(List.of("strings?4"));

        List<String> sublist = GraphGenerator.randomSetSizeSubList(messages, 2,true);

        assertFalse(sublist.isEmpty(), "Messages list is empty");
        assertEquals(1, sublist.size());

        String[] comps = sublist.getFirst().splitWithDelimiters("[!?$\\.]",0);
        assertEquals(".", comps[1]);
        assertEquals(Keywords.TYPE_PLACEHOLDER, comps[2]);
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
            String[] comps = message.splitWithDelimiters("[!?$\\.]",0);
            if (comps.length>1){
                assertTrue(decorations.containsKey(comps[0]), "Random message generated"+comps[0]);
                assertEquals(".", comps[1]);
                assertEquals(Keywords.TYPE_PLACEHOLDER, comps[2]);
            }
        }
    }
}
