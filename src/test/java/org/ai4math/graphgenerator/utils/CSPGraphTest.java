package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CSPGraphTest {

    @Test
    void givenNoVerticesAdded_whenGetInitialVertex_thenInitialVertexIsNull() throws IOException {
        CSPGraph g = new CSPGraph();

        CSPVertex initVertex = g.getInitialVertex();

        assertNull(initVertex, "initial vertex was not null but:"+initVertex);
    }

    @Test
    void givenVerticesAddedButInitialNotIndicated_whenGetInitialVertex_thenReturnRandomInitialVertex() throws IOException {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true);
        CSPVertex x2 = new CSPVertex("x2");
        CSPVertex x3 = new CSPVertex("x3");

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        RelationshipEdge e = g.addEdge(x1, x2);
        e.setLabel("label");
        g.addEdge(x2, x3);
        g.addEdge(x3, x1);

        CSPVertex initVertex = g.getInitialVertex();

        assertNotNull(initVertex, "initial vertex is null");
        assertTrue(g.vertexSet().contains(initVertex), "initial vertex" + initVertex+" not contained within the vertex set: "+g.vertexSet());

        CSPVertex getVertex = g.getInitialVertex();

        assertEquals(getVertex, initVertex, "the vertex was not the same on the second retrieval but: "+ getVertex);
    }


    @Test
    void givenVerticesAddedAndInitialIndicated_whenGetInitialVertex_thenReturnInitialVertex() throws IOException {
        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true);
        CSPVertex x2 = new CSPVertex("x2");
        CSPVertex x3 = new CSPVertex("x3");

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        g.setInitialVertex(x1);

        RelationshipEdge e = g.addEdge(x1, x2);
        e.setLabel("label");
        g.addEdge(x2, x3);
        g.addEdge(x3, x1);

        CSPVertex initVertex = g.getInitialVertex();

        assertEquals(x1, initVertex, "initial vertex" + initVertex + "not equal to that expected: " + x1);
    }
}