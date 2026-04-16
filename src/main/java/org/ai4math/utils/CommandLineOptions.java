package org.ai4math.utils;

public class CommandLineOptions {
    private String filePath;
    private boolean regenerateDataset;
    private int baseGraphs;
    private int combinedGraphs;
    private boolean decorations;

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
