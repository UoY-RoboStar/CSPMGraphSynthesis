package org.ai4math;

import org.ai4math.cspmtransformer.CSPMTransformer;
import org.ai4math.datasetgeneration.DatasetGenerator;
import org.ai4math.graphgenerator.GraphGenerator;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.utils.CommandLineOptions;
import org.ai4math.vandv.FDRInvocation;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            CommandLineOptions parsedArgs = CommandLineOptions.parseCommandLine(args);

            System.out.println("Starting graph generation");
            GraphGenerator graphGenerator = new GraphGenerator(parsedArgs.getGraphGenerationOptions());
            List<CSPGraph> graphs = graphGenerator.generateGraphSet(
                    parsedArgs.getBaseGraphs(),
                    parsedArgs.getCombinedGraphs()
            );

            System.out.println("Starting transformation to csp");
            CSPMTransformer cspmTransformer = new CSPMTransformer();
            for (CSPGraph graph: graphs) {
                cspmTransformer.graphToCSPM(parsedArgs.getFilePath(), graph,graph.getInitialVertex().getName());
            }
            List<String> cspFiles = cspmTransformer.getCspFiles(parsedArgs.isRegenerateDataset());

            System.out.println("Starting dataset generation");
            DatasetGenerator datasetGenerator = new DatasetGenerator(parsedArgs.getFilePath(),"Dataset.csv");

            System.out.println("Starting csp verification");
            for (String file: cspFiles){
                FDRInvocation fdrInvocation = new FDRInvocation();
                fdrInvocation.performVerification(file);

                datasetGenerator.addEntryToDataSet(Files.readString(Path.of(file)),fdrInvocation.getFdrOutput());
            }
        } catch (ParseException | NumberFormatException exception) {
            System.exit(1);
        }
    }

}