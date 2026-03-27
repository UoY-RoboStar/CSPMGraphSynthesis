package org.ai4math;

import org.ai4math.cspmtransformer.CSPMTransformer;
import org.ai4math.datasetgeneration.DatasetGenerator;
import org.ai4math.graphgenerator.GraphGenerator;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.vandv.FDRInvocation;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        List<Integer> parsedArgs = parseCommandLine(args);

        GraphGenerator graphGenerator = new GraphGenerator();
        List<CSPGraph> graphs = graphGenerator.generateGraphSet(parsedArgs.get(0), parsedArgs.get(1));

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        for (CSPGraph graph: graphs) {
            cspmTransformer.graphToCSPM(graph,graph.getInitialVertex().getName());
        }
        List<String> cspFiles = cspmTransformer.getCspFiles();

        DatasetGenerator datasetGenerator = new DatasetGenerator("Dataset.csv");

        for (String file: cspFiles){
            FDRInvocation fdrInvocation = new FDRInvocation();
            fdrInvocation.performVerification(file);

            datasetGenerator.addEncodedCspEntryToDataSet(Files.readString(Path.of(file)),fdrInvocation.getFdrOutput());
        }

    }

    private static List<Integer> parseCommandLine(String[] args) throws IOException {
        Options options = new Options();
        Option baseGraph = new Option("b", "baseGraphs",
                true, "Number of base graphs to create");
        baseGraph.setRequired(true);
        options.addOption(baseGraph);

        Option combinedGraph = new Option("c", "combinedGraphs",
                true, "Number of combined graphs to create");
        combinedGraph.setRequired(true);
        options.addOption(combinedGraph);

        HelpFormatter formatter = HelpFormatter.builder().get();
        String header = "Provide the required options to operate the synthesiser";
        String footer = "Please report issues to https://github.com/UoY-RoboStar/CSPMGraphSynthesis";

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = commandLineParser.parse(options, args);
            String b = cmd.getOptionValue("b");
            String c = cmd.getOptionValue("c");
            int b_int = Integer.parseInt(b);
            int c_int = Integer.parseInt(c);

            return List.of(b_int,c_int);
        } catch (ParseException | NumberFormatException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("CSPMGraphSynthesis", header, options, footer,true);

            System.exit(1);
        }

        return List.of(null,null);
    }
}