package org.ai4math.vandv.utils;

import org.ai4math.cspm.Keywords;

import java.util.*;
import java.util.stream.IntStream;

public class FDRCounterexample {
    private String type;
    private List<String> trace;
    private List<String> revealedTrace;
    private List<String> revealedProcessesTrace;
    private List<String> processesTrace;
    private List<String> noTauTrace;
    private Set<String> hidden;
    private String machineName;

    public Set<String> getHidden() {
        return hidden;
    }

    public void addHidden() {
        Set<String> hiddenSet = new HashSet<>(Set.of());
        if (this.processesTrace != null && this.revealedProcessesTrace != null) {
            int[] indexes = IntStream.range(0, this.processesTrace.size())
                    .filter(i -> Objects.equals(this.processesTrace.get(i), Keywords.TAU)).toArray();

            for (int index : indexes) {
                hiddenSet.add(this.revealedProcessesTrace.get(index));
            }
        }
        this.hidden = hiddenSet;
    }

    public void convertTraceToProcesses(Map<String, String> eventMap){
        if (this.trace.isEmpty()){
            this.processesTrace = List.of();
        } else {
            for (String entry : this.trace) {
                String convertedEntry = eventMap.get(entry);
                if (this.processesTrace == null) {
                    this.processesTrace = new ArrayList<>(List.of(convertedEntry));
                } else {
                    this.processesTrace.add(convertedEntry);
                }
            }
        }
    }

    public void convertRevealedTraceToProcesses(Map<String, String> eventMap){
        if (this.revealedTrace.isEmpty()){
            this.revealedProcessesTrace = List.of();
        } else {
            for (String entry : this.revealedTrace) {
                String convertedEntry = eventMap.get(entry);
                if (this.revealedProcessesTrace == null) {
                    this.revealedProcessesTrace = new ArrayList<>(List.of(convertedEntry));
                } else {
                    this.revealedProcessesTrace.add(convertedEntry);
                }
            }
        }

        setNoTauTrace();
        addHidden();
    }

    public List<String> getProcessesTrace() {
        return processesTrace;
    }

    public List<String> getTrace() {
        return trace;
    }

    public void setTrace(List<String> trace) {
        this.trace = trace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRevealedTrace() {
        return revealedTrace;
    }

    public void setRevealedTrace(List<String> revealedTrace) {
        this.revealedTrace = revealedTrace;
    }

    public List<String> getRevealedProcessesTrace() {
        return revealedProcessesTrace;
    }

    public void setRevealedProcessesTrace(List<String> revealedProcessesTrace) {
        this.revealedProcessesTrace = revealedProcessesTrace;
    }

    public List<String> getNoTauTrace() {
        return noTauTrace;
    }

    public void setNoTauTrace() {
        List<String> noTauTrace = new ArrayList<>(this.processesTrace);
        noTauTrace.removeIf(n -> Objects.equals(n, Keywords.TAU));
        this.noTauTrace = noTauTrace;
    }
}
