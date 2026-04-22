package org.ai4math.utils;

import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.IOException;

public class CommandLineOptions {
    private String filePath;
    private boolean regenerateDataset;
    private int baseGraphs;
    private int combinedGraphs;
    private boolean decorations;
    private boolean renaming;


    public static CommandLineOptions parseCommandLine(String[] args) throws IOException, ParseException {
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

        Option decorations = new Option("d", "decorationsIncluded",
                false, "Include decorations, with data passing, for channels");
        decorations.setRequired(false);
        options.addOption(decorations);

        Option renaming = new Option("re", "renamingsIncluded",
                false, "Include renaming of channels");
        renaming.setRequired(false);
        options.addOption(renaming);


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
            commandLineOptions.setDecorations(cmd.hasOption(decorations));
            commandLineOptions.setRenaming(cmd.hasOption(renaming));
            commandLineOptions.setBaseGraphs(Integer.parseInt(b));
            commandLineOptions.setCombinedGraphs(Integer.parseInt(c));
            commandLineOptions.setFilePath(p);

            return commandLineOptions;
        } catch (ParseException | NumberFormatException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("CSPMGraphSynthesis", header, options, footer,true);

            throw e;
        }
    }

    public int getBaseGraphs() {
        return baseGraphs;
    }

    public int getCombinedGraphs() {
        return combinedGraphs;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isRegenerateDataset() {
        return regenerateDataset;
    }

    public boolean isDecorated() {return decorations;}

    public void setDecorations(boolean decorations) {
        this.decorations = decorations;
    }

    public boolean isRenaming() {
        return renaming;
    }

    public void setRenaming(boolean renaming) {
        this.renaming = renaming;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setBaseGraphs(int baseGraphs) {
        this.baseGraphs = baseGraphs;
    }

    public void setRegenerateDataset(boolean regenerateDataset) {
        this.regenerateDataset = regenerateDataset;
    }

    public void setCombinedGraphs(int combinedGraphs) {
        this.combinedGraphs = combinedGraphs;
    }
}
