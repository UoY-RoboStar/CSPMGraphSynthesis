package org.ai4math.graphgenerator.utils;

import org.jgrapht.graph.DefaultEdge;

public class RelationshipEdge extends DefaultEdge {
    private String label;

    /*
     * Constructs a relationship edge
     */
    public RelationshipEdge() {
        this(null);
    }

    /*
     * Constructs a relationship edge
     *
     * @param label the label of the new edge.
     */
    public RelationshipEdge(String label){
        this.label = label;
    }

    /*
     * Gets the label associated with the edge
     *
     * @return edge label
     */
    public String getLabel() {
        return label;
    }

    /*
     * Sets the label associated with the edge
     *
     * @param label the new label of the edge.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        if (label != null) {
            return "(" + getSource() + ":" + getTarget() + ":" + label + ")";
        }
        return "(" + getSource() + ":" + getTarget() + ")";
    }
}
