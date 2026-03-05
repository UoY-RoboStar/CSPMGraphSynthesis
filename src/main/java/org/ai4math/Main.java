package org.ai4math;

import org.ai4math.cspmtransformer.CSPMTransformer;
import org.ai4math.datasetgeneration.DatasetGenerator;
import org.ai4math.graphgenerator.GraphGenerator;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.vandv.FDRInvocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        GraphGenerator graphGenerator = new GraphGenerator();
        List<CSPGraph> graphs = graphGenerator.GenerateGraphSet();

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        for (CSPGraph graph: graphs) {
            cspmTransformer.GraphToCSPM(graph,graph.getInitialVertex().getName());
        }
        List<String> cspFiles = cspmTransformer.getCspFiles();

        DatasetGenerator datasetGenerator = new DatasetGenerator("Dataset.csv");

        for (String file: cspFiles){
            FDRInvocation fdrInvocation = new FDRInvocation();
            fdrInvocation.performVerification(file);

            datasetGenerator.addEncodedCspEntryToDataSet(Files.readString(Path.of(file)),fdrInvocation.getFdrOutput());
        }


    }
}