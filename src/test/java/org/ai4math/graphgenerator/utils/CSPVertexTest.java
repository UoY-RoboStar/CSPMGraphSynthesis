package org.ai4math.graphgenerator.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

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

        assertTrue(vertex.isInternalChoice(), "vertex not marked as an internal choice process");
    }

    @Test
    void givenVertexAdded_whenSetInterleaveProcess_thenIsInterleaveProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setInterleave(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isInterleave(), "vertex not marked as an interleave process");
    }

    @Test
    void givenVertexAdded_whenSetExceptionProcess_thenIsExceptionProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setException(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isException(), "vertex not marked as an exception process");
    }

    @Test
    void givenVertexAdded_whenSetTimeoutProcess_thenIsTimeoutProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setTimeout(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isTimeout(), "vertex not marked as a timeout process");
    }

    @Test
    void givenVertexAdded_whenSetInterruptProcess_thenIsInterruptProcessReturnTrue() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setInterrupt(true);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertTrue(vertex.isInterrupt(), "vertex not marked as an interrupt process");
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
    void givenVertexAdded_whenSetProject_thenGetProjectReturnsChannels() {
        Set<String> project = new HashSet<>(Set.of("testingchannel", "secondchannel"));

        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setProjected(project);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals(project, vertex.getProjected(), "projected channels of vertex are unexpected");
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


    @Test
    void givenVertexAdded_whenSetParameter_thenParameterReturnedThroughGetParameter() {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1");
        Pair<String,String> parameter = Pair.of("TestName", "Boolean");
        x1.setParameter(parameter);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals("TestName", vertex.getParameter().getKey(), "parameter name is unexpected");
        assertEquals("Boolean", vertex.getParameter().getValue(), "parameter name is unexpected");
    }


    @Test
    void givenVertexAdded_whenSetScopedVars_thenScopedVarsReturnedThroughGetScopedVars() {
        Map<String, String> vars = new TreeMap<>(Map.of("testingProcess1", "int",
                "testingProcess2", "bool"));
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setScopedVars(vars);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals(vars, vertex.getScopedVars(), "scoped variables are unexpected");
    }

    @Test
    void givenVertexAdded_whenSetRenaming_thenGetRenamingReturnsChannelsMapping(){
        Map<String, String> renaming = new TreeMap<>(Map.of("testingProcess1", "testingProcess2",
                "testingProcess2", "testingProcess3"));
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        x1.setRenaming(renaming);
        g.addVertex(x1);

        CSPVertex vertex = g.vertexSet().iterator().next();

        assertEquals(renaming, vertex.getRenaming(), "renaming channels of vertex are unexpected");

    }
}
