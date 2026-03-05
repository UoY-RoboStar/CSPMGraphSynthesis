package org.ai4math.graphgenerator.utils;

import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Collections.list;

// DirectedPseudograph enables loops and multiple parallel edges
public class CSPGraph extends DirectedPseudograph<CSPVertex,RelationshipEdge> {
    private CSPVertex initialVertex;

    public CSPGraph() {
        super(RelationshipEdge.class);
    }

    public CSPGraph(Supplier vertexSupplier, Supplier edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }

    public CSPVertex getInitialVertex() {
        if (this.initialVertex == null) {
            Set<CSPVertex> allVertices = this.vertexSet();
            if (!allVertices.isEmpty()) {
                List<CSPVertex> vertexList = new ArrayList<>(allVertices);
                Random rand = new Random();
                List<CSPVertex> initials = getInitialVertices();
                if (initials.isEmpty()) {
                    this.initialVertex = vertexList.get(rand.nextInt(vertexList.size()));
                } else {
                    this.initialVertex = initials.get(rand.nextInt(initials.size()));
                }
            }
        }
        return initialVertex;
    }

    public void setInitialVertex(CSPVertex initialVertex) {
        this.initialVertex = initialVertex;
    }

    private List<CSPVertex> getInitialVertices(){
        List<CSPVertex> initials = new ArrayList<>();
        List<CSPVertex> vertexList = new ArrayList<>(this.vertexSet());
        for (CSPVertex vertex: vertexList){
            if (vertex.isInitialVertex())
            {
               initials.add(vertex);
            }
        }
        return initials;
    }
}
