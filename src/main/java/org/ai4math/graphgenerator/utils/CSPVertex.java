package org.ai4math.graphgenerator.utils;

import java.util.List;
import java.util.Set;

public class CSPVertex {
    private boolean initialVertex;
    private boolean processVertex;
    private boolean stopVertex;
    private boolean skipVertex;
    private boolean seqCompositionVertex;
    private boolean externalChoice;
    private boolean internalChoice;
    private boolean generalisedParallel;
    private boolean alphabetisedParallel;
    private boolean interleave;
    private List<Set<String>> alphabet;
    private String name;
    private Set<String> hidden;

    public CSPVertex(String name, boolean initialVertex){
        this.initialVertex = initialVertex;
        this.processVertex = false;
        this.name = name;
        this.stopVertex = false;
        this.skipVertex = false;
        this.seqCompositionVertex = false;
        this.internalChoice = false;
        this.externalChoice = false;
        this.generalisedParallel = false;
        this.alphabetisedParallel = false;
        this.interleave = false;
        this.alphabet = List.of(Set.of());
        this.hidden = Set.of();
    }

    public CSPVertex(String name, boolean initialVertex, boolean processVertex){
        this.initialVertex = initialVertex;
        this.processVertex = processVertex;
        this.name = name;
        this.stopVertex = false;
        this.skipVertex = false;
        this.seqCompositionVertex = false;
        this.internalChoice = false;
        this.externalChoice = false;
        this.generalisedParallel = false;
        this.alphabetisedParallel = false;
        this.interleave = false;
        this.alphabet = List.of(Set.of());
        this.hidden = Set.of();
    }

    public CSPVertex(String name){
        this.initialVertex = false;
        this.processVertex = false;
        this.name = name;
        this.stopVertex = false;
        this.skipVertex = false;
        this.seqCompositionVertex = false;
        this.internalChoice = false;
        this.externalChoice = false;
        this.generalisedParallel = false;
        this.alphabetisedParallel = false;
        this.interleave = false;
        this.alphabet = List.of(Set.of());
        this.hidden = Set.of();
    }

    public void setInitialVertex(boolean initialVertex) {
        this.initialVertex = initialVertex;
    }

    public boolean isInitialVertex() {
        return initialVertex;
    }

    public void setProcessVertex(boolean processVertex) {
        this.processVertex = processVertex;
    }

    public boolean isProcessVertex() {
        return processVertex;
    }

    public boolean isSkipVertex() {
        return skipVertex;
    }

    public boolean isStopVertex() {
        return stopVertex;
    }

    public void setSkipVertex(boolean skipVertex) {
        this.skipVertex = skipVertex;
    }

    public void setStopVertex(boolean stopVertex) {
        this.stopVertex = stopVertex;
    }

    public boolean isSeqCompositionVertex() {
        return seqCompositionVertex;
    }

    public void setSeqCompositionVertex(boolean compositionVertex) {
        this.seqCompositionVertex = compositionVertex;
    }

    public void setExternalChoice(boolean externalChoice) {
        this.externalChoice = externalChoice;
    }

    public void setInternalChoice(boolean internalChoice) {
        this.internalChoice = internalChoice;
    }

    public boolean isInternalChoice() {
        return internalChoice;
    }

    public boolean isExternalChoice() {
        return externalChoice;
    }

    public boolean isAlphabetisedParallel() {
        return alphabetisedParallel;
    }

    public boolean isGeneralisedParallel() {
        return generalisedParallel;
    }

    public void setAlphabetisedParallel(boolean alphabetisedParallel) {
        this.alphabetisedParallel = alphabetisedParallel;
    }

    public void setGeneralisedParallel(boolean generalisedParallel) {
        this.generalisedParallel = generalisedParallel;
    }

    public boolean isInterleave() {
        return interleave;
    }

    public void setInterleave(boolean interleave) {
        this.interleave = interleave;
    }

    public void setAlphabet(List<Set<String>> alphabet) {
        this.alphabet = alphabet;
    }

    public List<Set<String>> getAlphabet() {
        return alphabet;
    }

    public void setHidden(Set<String> hidden) {
        this.hidden = hidden;
    }

    public Set<String> getHidden() {
        return hidden;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CSPVertex) && (toString().equals(obj.toString()));
    }
}
