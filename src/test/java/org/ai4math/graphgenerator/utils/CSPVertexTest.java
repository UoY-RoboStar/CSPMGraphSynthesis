package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSPVertexTest {
    @Test
    void givenSetInitialVertex_whenIsInitial_thenReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1");
        x1.setInitialVertex(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isInitialVertex(), "vertex not marked as an initial vertex");
    }

    @Test
    void givenProcessVerticesAdded_whenIsProcess_thenReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isProcessVertex(), "vertex not marked as a process");
    }


    @Test
    void givenVertexAdded_whenSetProcess_thenIsProcessReturnsTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true);
        x1.setProcessVertex(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isProcessVertex(), "vertex not marked as a process");
    }


    @Test
    void givenVertexAdded_whenSetSequentialProcess_thenIsSequentialProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setSeqCompositionVertex(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isSeqCompositionVertex(), "vertex not marked as a seq comp process");
    }


    @Test
    void givenVertexAdded_whenSetAlphabetisedParallelProcess_thenIsAlphabetisedParallelProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setAlphabetisedParallel(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isAlphabetisedParallel(), "vertex not marked as a alphabetised par process");
    }

    @Test
    void givenVertexAdded_whenSetGeneralisedParallelProcess_thenIsGeneralisedParallelProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setGeneralisedParallel(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isGeneralisedParallel(), "vertex not marked as a gen par process");
    }

    @Test
    void givenVertexAdded_whenSetExternalChoiceProcess_thenIsExternalChoiceProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setExternalChoice(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isExternalChoice(), "vertex not marked as a external choice process");
    }

    @Test
    void givenVertexAdded_whenSetInternalChoiceProcess_thenIsInternalChoiceProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setInternalChoice(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isInternalChoice(), "vertex not marked as a internal choice process");
    }

    @Test
    void givenVertexAdded_whenSetInterleaveProcess_thenIsInterleaveProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setInterleave(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isInterleave(), "vertex not marked as a interleave process");
    }

    @Test
    void givenVertexAdded_whenSetHidden_thenGetHiddenReturnsChannels() {
        Set<String> hidden = new HashSet<>(Set.of("testingchannel", "secondchannel"));

        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setHidden(hidden);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals(hidden, vertex.getHidden(), "hidden channels of vertex are unexpected");
    }


    @Test
    void givenVertexAdded_whenSetAlphabet_thenGetAlphabetReturnsChannels() {
        Set<String> alphabet1 = new HashSet<>(Set.of("testingchannel", "secondchannel"));
        Set<String> alphabet2 = new HashSet<>(Set.of("secondchannel"));
        List<Set<String>> alphabets = List.of(alphabet2, alphabet1);

        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setAlphabet(alphabets);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals(alphabets, vertex.getAlphabet(), "alphabets of vertex are unexpected");
    }

    @Test
    void givenVertexAdded_whenSetSkipProcess_thenIsSkipProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setSkipVertex(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isSkipVertex(), "vertex not marked as a skip vertex");
    }

    @Test
    void givenVertexAdded_whenSetStopProcess_thenIsStopProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setStopVertex(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isStopVertex(), "vertex not marked as a stop vertex");
    }

    @Test
    void givenVertexAdded_whenSetName_thenNameReturnedThroughGetName() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1");
        x1.setName("TestName");
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals("TestName", vertex.getName(), "vertex name is unexpected");
    }
}
