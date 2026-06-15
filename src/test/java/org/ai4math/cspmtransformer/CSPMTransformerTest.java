package org.ai4math.cspmtransformer;

import net.jcip.annotations.NotThreadSafe;
import org.ai4math.cspm.Keywords;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.graphgenerator.utils.CSPVertex;
import org.ai4math.graphgenerator.utils.RelationshipEdge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@NotThreadSafe
public class CSPMTransformerTest {

    public static String resourcePath;
    public static Path dir;

    @BeforeAll
    public static void Before(){
        resourcePath = System.getProperty("user.home");
        dir = Paths.get(resourcePath, "test", "cspmtransformer");
        resourcePath = dir.toAbsolutePath().toString();
    }

    @AfterEach
    public void After(){
        List<String> files = List.of();

        try (Stream<Path> paths = Files.walk(Paths.get(resourcePath))) {
            files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            System.out.println("Exception encountered when retrieving files: " + e.getMessage());
        }

        for (String file : files) {
            File filepath = new File(file);
            filepath.delete();
        }
    }

    @Test
    public void givenRegenTrue_whenGetCSPFiles_thenAllCSPFilesReturned() throws IOException{
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        String fileName = "FileTest.csp";
        String additionalFile = "AdditionalFileTest.csp";

        File filePath = new File(Paths.get(resourcePath,"CSPMGraphSynthesis", fileName).toString());
        filePath.createNewFile();
        File additionalFilePath = new File(Paths.get(resourcePath,"CSPMGraphSynthesis", additionalFile).toString());
        additionalFilePath.createNewFile();

        CSPMTransformer cspmTransformer = new CSPMTransformer(resourcePath);
        List<String> cspFiles = cspmTransformer.getCspFiles(true);

        assertEquals(2, cspFiles.size(), "An incorrect number of files were gathered: "+cspFiles);
        assertTrue(cspFiles.contains(filePath.toString()), "File path "+filePath+" is missing from "+cspFiles);
        assertTrue(cspFiles.contains(additionalFilePath.toString()), "File path "+additionalFilePath+" is missing from "+cspFiles);

        filePath.delete();
        additionalFilePath.delete();
    }

    @Test
    public void givenRegenFalse_whenGetCSPFiles_thenOnlyNewCSPFileReturned() throws IOException{
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        String fileName = "FileTest.csp";
        String additionalFile = "AdditionalFileTest.csp";

        File filePath = new File(Paths.get(resourcePath,"CSPMGraphSynthesis", fileName).toString());
        filePath.createNewFile();
        File additionalFilePath = new File(Paths.get(resourcePath,"CSPMGraphSynthesis", additionalFile).toString());
        additionalFilePath.createNewFile();

        CSPMTransformer cspmTransformer = new CSPMTransformer(resourcePath);
        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(0, cspFiles.size(), "An incorrect number of files were gathered: "+cspFiles);

        filePath.delete();
        additionalFilePath.delete();
    }

    @Test
    public void givenBasicGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five?'a' -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'a' -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenIntDecoratedGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one$5 -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five.aefuoafh -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one$5 -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five.aefuoafh -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        String type = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<7;i++) {
                String channel = br.readLine();
                if (channel.contains("channel one")) {
                    String[] parts = channel.split(":");
                    assertTrue(parts[1].stripLeading().matches("\\{[0-9]*\\.\\.[0-9]*\\}"), "The number set isn't a valid format: "+parts[1]);
                    String value = parts[1].replaceAll("[{}]","");
                    String[] figures = value.split("\\.");
                    int lower = Integer.parseInt(figures[0].strip());
                    int upper = Integer.parseInt(figures[2].strip());
                    assertTrue(lower>=0 && lower<=5, "Value of lower bound is unexpected: "+lower );
                    assertTrue(upper>=lower && upper<lower+10, "Value of upper bound is unexpected: "+upper );
                } else if (channel.contains("channel five")){
                    String[] parts = channel.split(":");
                    String[] datatype = type.split(" ");
                    assertEquals(datatype[1],parts[1].strip(), "Data types do not match");
                } else if (channel.contains("channel")){
                    assertTrue(channels.contains(channel), "Channel " + channel + " was not expected");
                } else {
                    type = channel;
                    String[] parts = channel.split("=");
                    String[] datatype = parts[0].split(" ");
                    assertEquals(Keywords.DATATYPE, datatype[0], "Datatype incorrectly defined: "+channel);
                    assertTrue(parts[1].contains("aefuoafh"), "Datatype does not include aefuoafh within: "+channel);
                }
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenIntMultiDataTypeDecoratedGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one$5 -> two -> five.nimo -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five.aefuoafh -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one$5 -> two -> five.nimo -> three -> Interim").append("\n")
                .append("Interim = four -> five.aefuoafh -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        String type = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<7;i++) {
                String channel = br.readLine();
                if (channel.contains("channel one")) {
                    String[] parts = channel.split(":");
                    assertTrue(parts[1].stripLeading().matches("\\{[0-9]*\\.\\.[0-9]*\\}"), "The number set isn't a valid format: "+parts[1]);
                    String value = parts[1].replaceAll("[{}]","");
                    String[] figures = value.split("\\.");
                    int lower = Integer.parseInt(figures[0].strip());
                    int upper = Integer.parseInt(figures[2].strip());
                    assertTrue(lower>=0 && lower<=5, "Value of lower bound is unexpected: "+lower );
                    assertTrue(upper>=lower && upper<lower+10, "Value of upper bound is unexpected: "+upper );
                } else if (channel.contains("channel five")){
                    String[] parts = channel.split(":");
                    String[] datatype = type.split(" ");
                    assertEquals(datatype[1],parts[1].strip(), "Data types do not match");
                } else if (channel.contains("channel")){
                    assertTrue(channels.contains(channel), "Channel " + channel + " was not expected");
                } else {
                    type = channel;
                    String[] parts = channel.split("=");
                    String[] datatype = parts[0].split(" ");
                    assertEquals(Keywords.DATATYPE, datatype[0], "Datatype incorrectly defined: "+channel);
                    assertTrue(parts[1].contains("aefuoafh"), "Datatype does not include aefuoafh within: "+channel);
                    assertTrue(parts[1].contains("nimo"), "Datatype does not include nimo within: "+channel);
                }
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenGraphWithHiding_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);

        Set<String> hidden = Set.of("five");
        interimVertex.setHidden(hidden);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = (four -> five -> six -> SKIP)\\{five}").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraphWithHiding_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Set<String> hidden = Set.of("five");
        initialVertex.setHidden(hidden);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five?'a' -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = (one!false -> two -> three -> Interim)\\{five}").append("\n")
                .append("Interim = four -> five?'a' -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraphWithHidingWithExtraChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Set<String> hidden = Set.of("five.'b'", "seven.true");
        initialVertex.setHidden(hidden);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five?'a' -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");
        channels.add("channel seven : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Interim = four -> five?'a' -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<7;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }
            String[] initial = br.readLine().split("\\\\");
            String initialString = "Initial = (one!false -> two -> three -> Interim)";
            String[] hiddenParts = initial[1].replace("{","").replace("}","").split(",");

            assertEquals(initialString, initial[0], "File contents is unexpected: " + initial[0]);
            assertTrue(hiddenParts[0].equals("seven.true")||hiddenParts[0].equals("five.'b'"),
                    "Unexpected hidden channel " + hiddenParts[0]);
            assertTrue(hiddenParts[1].equals("seven.true")||hiddenParts[1].equals("five.'b'"),
                    "Unexpected hidden channel " + hiddenParts[1]);

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraphWithHidingWithExtraDatatypeChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Set<String> hidden = Set.of("five.nimo", "seven.true");
        initialVertex.setHidden(hidden);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five.aefuoafh -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel six");
        channels.add("channel seven : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Interim = four -> five.aefuoafh -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        String type = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<8;i++){
                String channel = br.readLine();
                if (channel.contains("channel five")){
                    String[] parts = channel.split(":");
                    String[] datatype = type.split(" ");
                    assertEquals(datatype[1],parts[1].strip(), "Data types do not match");
                } else if (channel.contains("channel")){
                    assertTrue(channels.contains(channel), "Channel " + channel + " was not expected");
                } else {
                    type = channel;
                    String[] parts = channel.split("=");
                    String[] datatype = parts[0].split(" ");
                    assertEquals(Keywords.DATATYPE, datatype[0], "Datatype incorrectly defined: "+channel);
                    assertTrue(parts[1].contains("aefuoafh"), "Datatype does not include aefuoafh within: "+channel);
                    assertFalse(parts[1].substring(parts[1].indexOf("aefuoafh")+8).contains("aefuoafh"),
                            "Datatype does contains duplicate aefuoafh within: "+channel);
                    assertTrue(parts[1].contains("nimo"), "Datatype does not include nimo within: "+channel);
                }
            }
            String[] initial = br.readLine().split("\\\\");
            String initialString = "Initial = (one!false -> two -> three -> Interim)";
            String[] hiddenParts = initial[1].replace("{","").replace("}","").split(",");

            assertEquals(initialString, initial[0], "File contents is unexpected: " + initial[0]);
            assertTrue(hiddenParts[0].equals("seven.true")||hiddenParts[0].equals("five.nimo"),
                    "Unexpected hidden channel " + hiddenParts[0]);
            assertTrue(hiddenParts[1].equals("seven.true")||hiddenParts[1].equals("five.nimo"),
                    "Unexpected hidden channel " + hiddenParts[1]);

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenIntMultiDataTypeAdditionalDecoratedGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one$5 -> two -> five.nimo -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five.aefuoafh -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one$5 -> two -> five.nimo -> three -> Interim").append("\n")
                .append("Interim = four -> five.aefuoafh -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        String type = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<7;i++) {
                String channel = br.readLine();
                if (channel.contains("channel one")) {
                    String[] parts = channel.split(":");
                    assertTrue(parts[1].stripLeading().matches("\\{[0-9]*\\.\\.[0-9]*\\}"), "The number set isn't a valid format: "+parts[1]);
                    String value = parts[1].replaceAll("[{}]","");
                    String[] figures = value.split("\\.");
                    int lower = Integer.parseInt(figures[0].strip());
                    int upper = Integer.parseInt(figures[2].strip());
                    assertTrue(lower>=0 && lower<=5, "Value of lower bound is unexpected: "+lower );
                    assertTrue(upper>=lower && upper<lower+10, "Value of upper bound is unexpected: "+upper );
                } else if (channel.contains("channel five")){
                    String[] parts = channel.split(":");
                    String[] datatype = type.split(" ");
                    assertEquals(datatype[1],parts[1].strip(), "Data types do not match");
                } else if (channel.contains("channel")){
                    assertTrue(channels.contains(channel), "Channel " + channel + " was not expected");
                } else {
                    type = channel;
                    String[] parts = channel.split("=");
                    String[] datatype = parts[0].split(" ");
                    assertEquals(Keywords.DATATYPE, datatype[0], "Datatype incorrectly defined: "+channel);
                    assertTrue(parts[1].contains("aefuoafh"), "Datatype does not include aefuoafh within: "+channel);
                    assertTrue(parts[1].contains("nimo"), "Datatype does not include nimo within: "+channel);
                }
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenGraphWithProject_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);

        Set<String> projected = Set.of("five");
        interimVertex.setProjected(projected);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = (four -> five -> six -> SKIP)|\\{five}").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraphWithProject_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Set<String> projected = Set.of("five");
        initialVertex.setProjected(projected);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five?'a' -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = (one!false -> two -> three -> Interim)|\\{five}").append("\n")
                .append("Interim = four -> five?'a' -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraphWithProjectWithExtraChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Set<String> projected = Set.of("five", "seven.true");
        initialVertex.setProjected(projected);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five?'a' -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");
        channels.add("channel seven : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Interim = four -> five?'a' -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<7;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }
            String[] initial = br.readLine().split("\\|\\\\");
            String initialString = "Initial = (one!false -> two -> three -> Interim)";
            String[] projectedParts = initial[1].replace("{","").replace("}","").split(",");

            assertEquals(initialString, initial[0], "File contents is unexpected: " + initial[0]);
            assertTrue(projectedParts[0].equals("seven.true")||projectedParts[0].equals("five"),
                    "Unexpected hidden channel " + projectedParts[0]);
            assertTrue(projectedParts[1].equals("seven.true")||projectedParts[1].equals("five"),
                    "Unexpected hidden channel " + projectedParts[1]);

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenGraphWithRenaming_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);

        Map<String,String> rename = new LinkedHashMap<>();
        rename.put("one", "five");
        rename.put("five", "six");
        initialVertex.setRenaming(rename);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = (one -> two -> three -> Interim)[[one<-five,five<-six]]").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGraphWithRenaming_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Map<String,String> rename = new LinkedHashMap<>();
        rename.put("one", "two");
        rename.put("five.a", "six.b");
        initialVertex.setRenaming(rename);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five?'a' -> six.'b'");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six : Char");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = (one!false -> two -> three -> Interim)[[one<-two,five.a<-six.b]]").append("\n")
                .append("Interim = four -> five?'a' -> six.'b' -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }


    @Test
    public void givenDecoratedGraphWithRenamingAndExtraChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        Map<String,String> rename = new LinkedHashMap<>();
        rename.put("one", "five");
        rename.put("five", "six");
        rename.put("seven.false", "one.false");
        initialVertex.setRenaming(rename);
        graph.addVertex(initialVertex);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");
        channels.add("channel seven : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = (one!false -> two -> three -> Interim)[[one<-five,five<-six,seven.false<-one.false]]").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<7;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenSeqCompGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex seqCompVertex = new CSPVertex("seq", false, true);
        seqCompVertex.setSeqCompositionVertex(true);
        graph.addVertex(seqCompVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,seqCompVertex);
        edge2.setLabel(Keywords.TICK);
        RelationshipEdge edge4 = graph.addEdge(interimVertex,skip);
        edge4.setLabel("four -> five -> six");
        RelationshipEdge edge3 = graph.addEdge(seqCompVertex,skip);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim; one -> six -> SKIP").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenParSeqCompGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex seqCompVertex = new CSPVertex("seq", false, true);
        seqCompVertex.setSeqCompositionVertex(true);
        graph.addVertex(seqCompVertex);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one","five", "six");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,genParVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(genParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(genParVertex,interimVertex);
        edge3.setLabel("one -> six");
        RelationshipEdge edge5 = graph.addEdge(genParVertex,seqCompVertex);
        edge5.setLabel(Keywords.TICK);
        RelationshipEdge edge6 = graph.addEdge(seqCompVertex,skip);
        edge6.setLabel("two -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> GenPar").append("\n")
                .append("GenPar = ((four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one -> six -> Interim)); two -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedSeqCompGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex seqCompVertex = new CSPVertex("seq", false, true);
        seqCompVertex.setSeqCompositionVertex(true);
        graph.addVertex(seqCompVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,seqCompVertex);
        edge2.setLabel(Keywords.TICK);
        RelationshipEdge edge4 = graph.addEdge(interimVertex,skip);
        edge4.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge3 = graph.addEdge(seqCompVertex,skip);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim; one!true -> six -> SKIP").append("\n")
                .append("Interim = four -> five?'b' -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenInternalChoiceGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex intChoiceVertex = new CSPVertex("Int", true, true);
        intChoiceVertex.setInternalChoice(true);
        graph.addVertex(intChoiceVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,intChoiceVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(intChoiceVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(intChoiceVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> Int").append("\n")
                .append("Int = (four -> SKIP) |~| (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Int :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedInternalChoiceGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex intChoiceVertex = new CSPVertex("Int", true, true);
        intChoiceVertex.setInternalChoice(true);
        graph.addVertex(intChoiceVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,intChoiceVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(intChoiceVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(intChoiceVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> Int").append("\n")
                .append("Int = (four -> SKIP) |~| (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Int :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenExternalChoiceGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex extChoiceVertex = new CSPVertex("Ext", true, true);
        extChoiceVertex.setExternalChoice(true);
        graph.addVertex(extChoiceVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,extChoiceVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(extChoiceVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(extChoiceVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> Ext").append("\n")
                .append("Ext = (four -> SKIP) [] (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Ext :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedExternalChoiceGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex extChoiceVertex = new CSPVertex("Ext", true, true);
        extChoiceVertex.setExternalChoice(true);
        graph.addVertex(extChoiceVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,extChoiceVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(extChoiceVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(extChoiceVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> Ext").append("\n")
                .append("Ext = (four -> SKIP) [] (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Ext :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenGenParGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one","five", "six");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,genParVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(genParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(genParVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> GenPar").append("\n")
                .append("GenPar = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGenParGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one.false","five.'n'", "six");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,genParVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(genParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(genParVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> GenPar").append("\n")
                .append("GenPar = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGenParGraphWithExtraChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one.false","five.'n'", "six", "seven", "eight.true");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,genParVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(genParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(genParVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");
        channels.add("channel seven");
        channels.add("channel eight : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> GenPar").append("\n")
                .append("GenPar = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<8; i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(5, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedGenParGraphWithExtraDatatypeChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one.false","five.nimo", "six", "seven", "eight.true");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,genParVertex);
        edge2.setLabel("four -> five.aefuoafh -> six");
        RelationshipEdge edge4 = graph.addEdge(genParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(genParVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel six");
        channels.add("channel seven");
        channels.add("channel eight : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five.aefuoafh -> six -> GenPar").append("\n")
                .append("GenPar = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        String type = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<9; i++){
                String channel = br.readLine();
                if (channel.contains("channel five")){
                    String[] parts = channel.split(":");
                    String[] datatype = type.split(" ");
                    assertEquals(datatype[1],parts[1].strip(), "Data types do not match");
                } else if (channel.contains("channel")){
                    assertTrue(channels.contains(channel), "Channel " + channel + " was not expected");
                } else {
                    type = channel;
                    String[] parts = channel.split("=");
                    String[] datatype = parts[0].split(" ");
                    assertEquals(Keywords.DATATYPE, datatype[0], "Datatype incorrectly defined: "+channel);
                    assertTrue(parts[1].contains("aefuoafh"), "Datatype does not include aefuoafh within: "+channel);
                    assertFalse(parts[1].substring(parts[1].indexOf("aefuoafh")+8).contains("aefuoafh"),
                            "Datatype does contains duplicate aefuoafh within: "+channel);
                    assertTrue(parts[1].contains("nimo"), "Datatype does not include nimo within: "+channel);
                }
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(5, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenExcepGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex excepVertex = new CSPVertex("Excep", true, true);
        excepVertex.setException(true);
        Set<String> alphabet = Set.of("one","five", "six");
        excepVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(excepVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,excepVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(excepVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(excepVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> Excep").append("\n")
                .append("Excep = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |> (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Excep :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedExcepGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex excepVertex = new CSPVertex("Excep", true, true);
        excepVertex.setException(true);
        Set<String> alphabet = Set.of("one.false","five.'n'","six");
        excepVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(excepVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,excepVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(excepVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(excepVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> Excep").append("\n")
                .append("Excep = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |> (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Excep :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedExcepGraphWithExtraChannels_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex excepVertex = new CSPVertex("Excep", true, true);
        excepVertex.setException(true);
        Set<String> alphabet = Set.of("one.false","five.'n'", "six", "seven", "eight.true");
        excepVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(excepVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,excepVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(excepVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(excepVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");
        channels.add("channel seven");
        channels.add("channel eight : Bool");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> Excep").append("\n")
                .append("Excep = (four -> SKIP) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |> (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Excep :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<8; i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String set = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(set.split(",")).toList();
            assertEquals(5, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenAlphParGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex alphParVertex = new CSPVertex("AlphPar", true, true);
        alphParVertex.setAlphabetisedParallel(true);
        Set<String> alphabet1 = Set.of("two","five", "six");
        Set<String> alphabet2 = Set.of("one","five");
        alphParVertex.setAlphabet(List.of(alphabet1,alphabet2));
        graph.addVertex(alphParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,alphParVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(alphParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(alphParVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> AlphPar").append("\n")
                .append("AlphPar = (four -> SKIP) [ {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} ] (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert AlphPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String sets = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            String[] setList = sets.split("\\} \\|\\| \\{");
            List<String> alpha1 = Arrays.stream(setList[0].split(",")).toList();
            assertEquals(3, alpha1.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet1){
                assertTrue(alpha1.contains(alph), "Alphabet is missing a channel: "+alph);
            }
            List<String> alpha2 = Arrays.stream(setList[1].split(",")).toList();
            assertEquals(2, alpha2.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet2){
                assertTrue(alpha2.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedAlphParGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex alphParVertex = new CSPVertex("AlphPar", true, true);
        alphParVertex.setAlphabetisedParallel(true);
        Set<String> alphabet1 = Set.of("two","five.'n'", "six");
        Set<String> alphabet2 = Set.of("one.false","five.'n'");
        alphParVertex.setAlphabet(List.of(alphabet1,alphabet2));
        graph.addVertex(alphParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,alphParVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(alphParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(alphParVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> AlphPar").append("\n")
                .append("AlphPar = (four -> SKIP) [ {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} ] (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert AlphPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String sets = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            String[] setList = sets.split("\\} \\|\\| \\{");
            List<String> alpha1 = Arrays.stream(setList[0].split(",")).toList();
            assertEquals(3, alpha1.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet1){
                assertTrue(alpha1.contains(alph), "Alphabet is missing a channel: "+alph);
            }
            List<String> alpha2 = Arrays.stream(setList[1].split(",")).toList();
            assertEquals(2, alpha2.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet2){
                assertTrue(alpha2.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenDecoratedAlphParGraphEnumType_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex alphParVertex = new CSPVertex("AlphPar", true, true);
        alphParVertex.setAlphabetisedParallel(true);
        Set<String> alphabet1 = Set.of("two","five.Forward", "six");
        Set<String> alphabet2 = Set.of("one.false","five.Backward");
        alphParVertex.setAlphabet(List.of(alphabet1,alphabet2));
        graph.addVertex(alphParVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,alphParVertex);
        edge2.setLabel("four -> five?Forward -> six");
        RelationshipEdge edge4 = graph.addEdge(alphParVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(alphParVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?Forward -> six -> AlphPar").append("\n")
                .append("AlphPar = (four -> SKIP) [ {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} ] (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert AlphPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String enumType = br.readLine();
            assertTrue(enumType.contains("datatype"), "Enum not defined as type: "+enumType);
            assertTrue(enumType.contains("Forward"), "Enum does not contain Forward: "+enumType);
            assertTrue(enumType.contains("Backward"), "Enum does not contain Backward: "+enumType);
            String[] type = enumType.replace("datatype ", "").split(" = ");
            for (int i = 1; i<7;i++){
                String channel = br.readLine();
                if (channel.startsWith("channel five")){
                    String[] channelType = channel.split(" : ");
                    assertEquals(type[0], channelType[1],
                            "The type of channel five ("+channelType[1]+") does not match the datatype: "
                                    +type[0]+" of "+enumType);
                } else {
                    assertTrue(channels.contains(channel), "Channel " + channel + " was not expected");
                }
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String sets = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            String[] setList = sets.split("\\} \\|\\| \\{");
            List<String> alpha1 = Arrays.stream(setList[0].split(",")).toList();
            assertEquals(3, alpha1.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet1){
                assertTrue(alpha1.contains(alph), "Alphabet is missing a channel: "+alph);
            }
            List<String> alpha2 = Arrays.stream(setList[1].split(",")).toList();
            assertEquals(2, alpha2.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet2){
                assertTrue(alpha2.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenInterleaveGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex interVertex = new CSPVertex("Inter", true, true);
        interVertex.setInterleave(true);
        graph.addVertex(interVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,interVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(interVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(interVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> Inter").append("\n")
                .append("Inter = (four -> SKIP) ||| (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Inter :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedInterleaveGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex interVertex = new CSPVertex("InterPar", true, true);
        interVertex.setInterleave(true);
        graph.addVertex(interVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,interVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(interVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(interVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> InterPar").append("\n")
                .append("InterPar = (four -> SKIP) ||| (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert InterPar :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenInterruptGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex interVertex = new CSPVertex("Inter", true, true);
        interVertex.setInterrupt(true);
        graph.addVertex(interVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,interVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(interVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(interVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> Inter").append("\n")
                .append("Inter = (four -> SKIP) /\\ (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Inter :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedInterruptGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex interVertex = new CSPVertex("InterPar", true, true);
        interVertex.setInterrupt(true);
        graph.addVertex(interVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,interVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(interVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(interVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> InterPar").append("\n")
                .append("InterPar = (four -> SKIP) /\\ (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert InterPar :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenTimeoutGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex timeVertex = new CSPVertex("Timeout", true, true);
        timeVertex.setTimeout(true);
        graph.addVertex(timeVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,timeVertex);
        edge2.setLabel("four -> five -> six");
        RelationshipEdge edge4 = graph.addEdge(timeVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(timeVertex,interimVertex);
        edge3.setLabel("one -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five -> six -> Timeout").append("\n")
                .append("Timeout = (four -> SKIP) [> (one -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Timeout :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenDecoratedTimeoutGraph_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex timeVertex = new CSPVertex("Timeout", true, true);
        timeVertex.setTimeout(true);
        graph.addVertex(timeVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,timeVertex);
        edge2.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge4 = graph.addEdge(timeVertex,skip);
        edge4.setLabel("four");
        RelationshipEdge edge3 = graph.addEdge(timeVertex,interimVertex);
        edge3.setLabel("one!true -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> Timeout").append("\n")
                .append("Timeout = (four -> SKIP) [> (one!true -> six -> Interim)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Timeout :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenComplexGraphWithSeqAndPar_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException{
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex seqCompVertex = new CSPVertex("seq", false, true);
        seqCompVertex.setSeqCompositionVertex(true);
        graph.addVertex(seqCompVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);
        CSPVertex stop = new CSPVertex("STOP");
        stop.setStopVertex(true);
        graph.addVertex(stop);
        CSPVertex alphParVertex = new CSPVertex("AlphPar", true, true);
        alphParVertex.setAlphabetisedParallel(true);
        Set<String> alphabet1 = Set.of("two","five.'n'", "six");
        Set<String> alphabet2 = Set.of("one.false","five.'n'");
        alphParVertex.setAlphabet(List.of(alphabet1,alphabet2));
        graph.addVertex(alphParVertex);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,seqCompVertex);
        edge2.setLabel(Keywords.TICK);
        RelationshipEdge edge4 = graph.addEdge(interimVertex,alphParVertex);
        edge4.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge3 = graph.addEdge(seqCompVertex,skip);
        edge3.setLabel("one!true -> six");
        RelationshipEdge edge5 = graph.addEdge(alphParVertex,stop);
        edge5.setLabel("four");
        RelationshipEdge edge6 = graph.addEdge(alphParVertex,skip);
        edge6.setLabel("one$false -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim; one!true -> six -> SKIP").append("\n")
                .append("Interim = four -> five?'b' -> six -> AlphPar").append("\n")
                .append("AlphPar = (four -> STOP) [ {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} ] (one$false -> six -> SKIP)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert AlphPar :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String sets = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            String[] setList = sets.split("\\} \\|\\| \\{");
            List<String> alpha1 = Arrays.stream(setList[0].split(",")).toList();
            assertEquals(3, alpha1.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet1){
                assertTrue(alpha1.contains(alph), "Alphabet is missing a channel: "+alph);
            }
            List<String> alpha2 = Arrays.stream(setList[1].split(",")).toList();
            assertEquals(2, alpha2.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet2){
                assertTrue(alpha2.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenComplexGraphWithSeqAndChoice_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException{
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex seqCompVertex = new CSPVertex("seq", false, true);
        seqCompVertex.setSeqCompositionVertex(true);
        graph.addVertex(seqCompVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);
        CSPVertex stop = new CSPVertex("STOP");
        stop.setStopVertex(true);
        graph.addVertex(stop);
        CSPVertex intChoiceVertex = new CSPVertex("Int", true, true);
        intChoiceVertex.setInternalChoice(true);
        graph.addVertex(intChoiceVertex);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,seqCompVertex);
        edge2.setLabel(Keywords.TICK);
        RelationshipEdge edge4 = graph.addEdge(interimVertex,skip);
        edge4.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge3 = graph.addEdge(seqCompVertex,intChoiceVertex);
        edge3.setLabel("one!true -> six");
        RelationshipEdge edge7 = graph.addEdge(intChoiceVertex, skip);
        edge7.setLabel("five.'l'");
        RelationshipEdge edge8 = graph.addEdge(intChoiceVertex, stop);
        edge8.setLabel("two");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim; one!true -> six -> Int").append("\n")
                .append("Interim = four -> five?'b' -> six -> SKIP").append("\n")
                .append("Int = (five.'l' -> SKIP) |~| (two -> STOP)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert Int :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenComplexGraphWithSeqAndParAndChoice_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException{
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex seqCompVertex = new CSPVertex("seq", false, true);
        seqCompVertex.setSeqCompositionVertex(true);
        graph.addVertex(seqCompVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);
        CSPVertex stop = new CSPVertex("STOP");
        stop.setStopVertex(true);
        graph.addVertex(stop);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one.false","five.'n'","six");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex extChoiceVertex = new CSPVertex("Ext", true, true);
        extChoiceVertex.setExternalChoice(true);
        graph.addVertex(extChoiceVertex);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,seqCompVertex);
        edge2.setLabel(Keywords.TICK);
        RelationshipEdge edge4 = graph.addEdge(interimVertex,genParVertex);
        edge4.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge3 = graph.addEdge(seqCompVertex,skip);
        edge3.setLabel("one!true -> six");
        RelationshipEdge edge5 = graph.addEdge(genParVertex,extChoiceVertex);
        edge5.setLabel("four");
        RelationshipEdge edge6 = graph.addEdge(genParVertex,extChoiceVertex);
        edge6.setLabel("one$false -> six");
        RelationshipEdge edge7 = graph.addEdge(extChoiceVertex, skip);
        edge7.setLabel("five.'l'");
        RelationshipEdge edge8 = graph.addEdge(extChoiceVertex, stop);
        edge8.setLabel("two");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim; one!true -> six -> SKIP").append("\n")
                .append("Interim = four -> five?'b' -> six -> GenPar").append("\n")
                .append("GenPar = (four -> Ext) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one$false -> six -> Ext)").append("\n")
            .append("Ext = (five.'l' -> SKIP) [] (two -> STOP)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]").append("\n")
                .append("assert Ext :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String sets = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(sets.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenComplexGraphWithParAndChoice_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException{
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);
        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);
        CSPVertex stop = new CSPVertex("STOP");
        stop.setStopVertex(true);
        graph.addVertex(stop);
        CSPVertex genParVertex = new CSPVertex("GenPar", true, true);
        genParVertex.setGeneralisedParallel(true);
        Set<String> alphabet = Set.of("one.false","five.'n'","six");
        genParVertex.setAlphabet(List.of(alphabet));
        graph.addVertex(genParVertex);
        CSPVertex intChoiceVertex = new CSPVertex("Int", true, true);
        intChoiceVertex.setInternalChoice(true);
        graph.addVertex(intChoiceVertex);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one!false -> two -> three");
        RelationshipEdge edge4 = graph.addEdge(interimVertex,genParVertex);
        edge4.setLabel("four -> five?'b' -> six");
        RelationshipEdge edge5 = graph.addEdge(genParVertex,intChoiceVertex);
        edge5.setLabel("four");
        RelationshipEdge edge6 = graph.addEdge(genParVertex,intChoiceVertex);
        edge6.setLabel("one$false -> six");
        RelationshipEdge edge7 = graph.addEdge(intChoiceVertex, skip);
        edge7.setLabel("five.'l'");
        RelationshipEdge edge8 = graph.addEdge(intChoiceVertex, stop);
        edge8.setLabel("two");

        List<String> channels = new ArrayList<>();
        channels.add("channel one : Bool");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five : Char");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = one!false -> two -> three -> Interim").append("\n")
                .append("Interim = four -> five?'b' -> six -> GenPar").append("\n")
                .append("GenPar = (four -> Int) [| {");
        String expectedCSPFileStart = sb.toString();

        sb = new StringBuilder();
        sb.append("} |] (one$false -> six -> Int)").append("\n")
                .append("Int = (five.'l' -> SKIP) |~| (two -> STOP)").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]").append("\n")
                .append("assert GenPar :[deadlock free]").append("\n")
                .append("assert Int :[deadlock free]");
        String expectedCSPFileEnd = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertTrue(content.startsWith(expectedCSPFileStart), "File contents is unexpected: " + content);
            assertTrue(content.endsWith(expectedCSPFileEnd), "File contents is unexpected: " + content);
            String sets = content.substring(expectedCSPFileStart.length(), content.length()-expectedCSPFileEnd.length());
            List<String> alpha = Arrays.stream(sets.split(",")).toList();
            assertEquals(3, alpha.size(), "The length of the alphabet is unexpected");
            for (String alph : alphabet){
                assertTrue(alpha.contains(alph), "Alphabet is missing a channel: "+alph);
            }
        }

        file.delete();
    }

    @Test
    public void givenGraphWithRenamingAndHiding_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);

        Map<String,String> rename = new LinkedHashMap<>();
        rename.put("one", "five");
        rename.put("five", "six");
        initialVertex.setRenaming(rename);
        Set<String> hidden = Set.of("five");
        initialVertex.setHidden(hidden);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = ((one -> two -> three -> Interim)\\{five})[[one<-five,five<-six]]").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenGraphWithRenamingAndHidingAndProjection_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);

        Map<String,String> rename = new LinkedHashMap<>();
        rename.put("one", "five");
        rename.put("five", "six");
        initialVertex.setRenaming(rename);
        Set<String> hidden = Set.of("five");
        initialVertex.setHidden(hidden);
        Set<String> project = Set.of("two");
        initialVertex.setProjected(project);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = (((one -> two -> three -> Interim)\\{five})|\\{two})[[one<-five,five<-six]]").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }

    @Test
    public void givenGraphWithRenamingAndProjection_whenGraphToCSPM_thenAccurateCSPFileGenerated() throws IOException {
        CSPGraph graph = new CSPGraph();
        CSPVertex initialVertex = new CSPVertex("Initial",true,true);
        graph.addVertex(initialVertex);

        Map<String,String> rename = new LinkedHashMap<>();
        rename.put("one", "five");
        rename.put("five", "six");
        initialVertex.setRenaming(rename);
        Set<String> project = Set.of("two");
        initialVertex.setProjected(project);

        CSPVertex interimVertex = new CSPVertex("Interim", true, true);
        graph.addVertex(interimVertex);
        CSPVertex skip = new CSPVertex("SKIP");
        skip.setSkipVertex(true);
        graph.addVertex(skip);

        RelationshipEdge edge1 = graph.addEdge(initialVertex,interimVertex);
        edge1.setLabel("one -> two -> three");
        RelationshipEdge edge2 = graph.addEdge(interimVertex,skip);
        edge2.setLabel("four -> five -> six");

        List<String> channels = new ArrayList<>();
        channels.add("channel one");
        channels.add("channel two");
        channels.add("channel three");
        channels.add("channel four");
        channels.add("channel five");
        channels.add("channel six");

        StringBuilder sb = new StringBuilder();
        sb.append("Initial = ((one -> two -> three -> Interim)|\\{two})[[one<-five,five<-six]]").append("\n")
                .append("Interim = four -> five -> six -> SKIP").append("\n")
                .append("assert Initial :[deadlock free]").append("\n")
                .append("assert Interim :[deadlock free]");
        String expectedCSPFile = sb.toString();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        String fileName = "BasicTest";
        String filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", fileName+".csp").toString();
        cspmTransformer.graphToCSPM(resourcePath, graph, fileName);

        List<String> cspFiles = cspmTransformer.getCspFiles(false);

        assertEquals(1,cspFiles.size(), "File not included in list.");
        assertEquals(filePath, cspFiles.getFirst(), "Filepath is unexpected: "+cspFiles.getFirst());

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i<6;i++){
                String channel = br.readLine();
                assertTrue(channels.contains(channel), "Channel "+channel+" was not expected");
            }

            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            assertEquals(expectedCSPFile, content, "File contents is unexpected: " + content);
        }

        file.delete();
    }
}
