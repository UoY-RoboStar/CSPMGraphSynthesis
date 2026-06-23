package org.ai4math.utils;

public class GraphGenerationOptions {
    private boolean decorations;
    private boolean renaming;
    private boolean version2;

    public GraphGenerationOptions(){}

    public GraphGenerationOptions(boolean decorations,
                                  boolean renaming,
                                  int version){
        this.decorations = decorations;
        this.renaming = renaming;
        if (version == 2) {
            this.version2 = true;
        }
    }

    public void setOptions(boolean dec, boolean ren, int version) {
        this.decorations = dec;
        this.renaming = ren;
        if (version == 2) {
            this.version2 = true;
        }
    }

    public boolean isRenaming() {
        return renaming;
    }

    public boolean isDecorations() {
        return decorations;
    }

    public boolean isVersion2() {
        return version2;
    }
}
