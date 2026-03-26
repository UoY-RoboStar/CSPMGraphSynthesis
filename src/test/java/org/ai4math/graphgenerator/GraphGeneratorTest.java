package org.ai4math.graphgenerator;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;
import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.Graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GraphGeneratorTest {

    @BeforeEach
    public void createGraph() throws IOException {
        File imgFile = new File("src/test/resources/graph.png");
        imgFile.createNewFile();
    }

    @Test
    void givenMultiGraph_whenWriteBufferedImage_thenFileShouldExist() throws IOException {

        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        CSPVertex x2 = new CSPVertex("x2", false, true);
        CSPVertex x3 = new CSPVertex("x3", false, true);

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        g.setInitialVertex(x1);

        RelationshipEdge e = g.addEdge(x1, x2);
        e.setLabel("label");
        g.addEdge(x2, x3);
        g.addEdge(x3, x1);

        JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                new JGraphXAdapter<CSPVertex, RelationshipEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/graph.png");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
    }

    @Test
    void givenMultiGraphWithSelfLoop_whenWriteBufferedImage_thenFileShouldExist() throws IOException {

        CSPGraph g = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        CSPVertex x2 = new CSPVertex("x2", false, true);
        CSPVertex x3 = new CSPVertex("x3", false, true);

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        g.setInitialVertex(x1);

        RelationshipEdge e = g.addEdge(x1, x2);
        e.setLabel("label");
        g.addEdge(x2, x3);
        g.addEdge(x3, x1);
        g.addEdge(x1, x1);

        JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                new JGraphXAdapter<CSPVertex, RelationshipEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/graph.png");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
    }

    @Test
    void givenTwoDisjointMultiGraphs_whenCombinedAndWriteBufferedImage_thenFileShouldExist() throws IOException {

        CSPGraph g = new CSPGraph();

        CSPGraph g2 = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        CSPVertex x2 = new CSPVertex("x2", false, true);
        CSPVertex x3 = new CSPVertex("x3", false, true);

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        RelationshipEdge e = g.addEdge(x1, x2);
        e.setLabel("label");
        g.addEdge(x2, x3);
        g.addEdge(x3, x1);

        CSPVertex x4 = new CSPVertex("x4", true, true);
        CSPVertex x5 = new CSPVertex("x5", false, true);
        CSPVertex x6 = new CSPVertex("x6", false, true);

        g2.addVertex(x4);
        g2.addVertex(x5);
        g2.addVertex(x6);

        RelationshipEdge e2 = g2.addEdge(x4, x5);
        e2.setLabel("label");
        g2.addEdge(x5, x6);
        g2.addEdge(x6, x4);

        Graphs.addGraph(g, g2);

        JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                new JGraphXAdapter<CSPVertex, RelationshipEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/graph.png");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
    }

    @Test
    void givenTwoConnectedMultiGraphs_whenCombinedAndWriteBufferedImage_thenFileShouldExist() throws IOException {

        CSPGraph g = new CSPGraph();

        CSPGraph g2 = new CSPGraph();

        CSPVertex x1 = new CSPVertex("x1", true, true);
        CSPVertex x2 = new CSPVertex("x2", false, true);
        CSPVertex x3 = new CSPVertex("x3", false, true);

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        RelationshipEdge e = g.addEdge(x1, x2);
        e.setLabel("label");
        g.addEdge(x2, x3);
        g.addEdge(x3, x1);

        CSPVertex x5 = new CSPVertex("x5", true, true);
        CSPVertex x6 = new CSPVertex("x6", false, true);

        g2.addVertex(x3);
        g2.addVertex(x5);
        g2.addVertex(x6);

        RelationshipEdge e2 = g2.addEdge(x3, x5);
        e2.setLabel("label");
        g2.addEdge(x5, x6);
        g2.addEdge(x6, x3);

        Graphs.addGraph(g, g2);

        JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                new JGraphXAdapter<CSPVertex, RelationshipEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/graph.png");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
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

        JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                new JGraphXAdapter<CSPVertex, RelationshipEdge>(graph);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/"+vertx.getFirst().getName()+".png");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
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

            JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                    new JGraphXAdapter<CSPVertex, RelationshipEdge>(cspGraph);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
            layout.execute(graphAdapter.getDefaultParent());

            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            File imgFile = new File("src/test/resources/"+vertx.getFirst().getName()+".png");
            ImageIO.write(image, "PNG", imgFile);

            assertTrue(imgFile.exists());
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

            JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                    new JGraphXAdapter<CSPVertex, RelationshipEdge>(cspGraph);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
            layout.execute(graphAdapter.getDefaultParent());

            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            File imgFile = new File("src/test/resources/"+vertx.getFirst().getName()+".png");
            ImageIO.write(image, "PNG", imgFile);

            assertTrue(imgFile.exists());
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

        JGraphXAdapter<CSPVertex, RelationshipEdge> graphAdapter =
                new JGraphXAdapter<CSPVertex, RelationshipEdge>(graph);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/test/resources/"+vertx.getFirst().getName()+".png");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
    }

}
