package org.ai4math;

import org.ai4math.cspmtransformer.CSPMTransformer;
import org.ai4math.datasetgeneration.DatasetGenerator;
import org.ai4math.graphgenerator.GraphGenerator;
import org.ai4math.graphgenerator.utils.CSPGraph;
import org.ai4math.utils.CommandLineOptions;
import org.ai4math.vandv.FDRInvocation;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        CommandLineOptions parsedArgs = parseCommandLine(args);

        GraphGenerator graphGenerator = new GraphGenerator();
        List<CSPGraph> graphs = graphGenerator.generateGraphSet(
                parsedArgs.getBaseGraphs(),
                parsedArgs.getCombinedGraphs());

        CSPMTransformer cspmTransformer = new CSPMTransformer();
        for (CSPGraph graph: graphs) {
            cspmTransformer.graphToCSPM(parsedArgs.getFilePath(), graph,graph.getInitialVertex().getName());
        }
        List<String> cspFiles = cspmTransformer.getCspFiles(parsedArgs.isRegenerateDataset());

        DatasetGenerator datasetGenerator = new DatasetGenerator(parsedArgs.getFilePath(),"Dataset.csv");

        for (String file: cspFiles){
            FDRInvocation fdrInvocation = new FDRInvocation();
            fdrInvocation.performVerification(file);

            datasetGenerator.addEncodedCspEntryToDataSet(Files.readString(Path.of(file)),fdrInvocation.getFdrOutput());
        }

    }

    private static CommandLineOptions parseCommandLine(String[] args) throws IOException {
        Options options = new Options();
        Option filePath = new Option("p", "filePath",
                true, "Path for the Dataset file and generated csp");
        filePath.setRequired(false);
        options.addOption(filePath);

        Option baseGraph = new Option("b", "baseGraphs",
                true, "Number of base graphs to create");
        baseGraph.setRequired(true);
        options.addOption(baseGraph);

        Option combinedGraph = new Option("c", "combinedGraphs",
                true, "Number of combined graphs to create");
        combinedGraph.setRequired(true);
        options.addOption(combinedGraph);

        Option regen = new Option("r", "regenerateDataset",
                false, "Regenerate the dataset using existing csp examples");
        regen.setRequired(false);
        options.addOption(regen);

        HelpFormatter formatter = HelpFormatter.builder().get();
        String header = "Provide the required options to operate the synthesiser";
        String footer = "Please report issues to https://github.com/UoY-RoboStar/CSPMGraphSynthesis";

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        CommandLineOptions commandLineOptions = new CommandLineOptions();

        try {
            cmd = commandLineParser.parse(options, args);
            String p = cmd.getOptionValue("p");
            String b = cmd.getOptionValue("b");
            String c = cmd.getOptionValue("c");

            commandLineOptions.setRegenerateDataset(cmd.hasOption(regen));
            commandLineOptions.setBaseGraphs(Integer.parseInt(b));
            commandLineOptions.setCombinedGraphs(Integer.parseInt(c));
            commandLineOptions.setFilePath(p);

            return commandLineOptions;
        } catch (ParseException | NumberFormatException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("CSPMGraphSynthesis", header, options, footer,true);

            System.exit(1);
        }

        return commandLineOptions;
    }
}