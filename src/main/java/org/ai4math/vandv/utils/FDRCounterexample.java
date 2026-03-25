package org.ai4math.vandv.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FDRCounterexample {
    private String type;
    private List<String> trace;
    private List<String> processesTrace;

    public void convertTraceToProcesses(Map<String, String> eventMap){
        for (String entry : this.trace){
            String convertedEntry = eventMap.get(entry);
            if (this.processesTrace == null) {
                this.processesTrace = new ArrayList<>(List.of(convertedEntry));
            }
            else {
                boolean added = this.processesTrace.add(convertedEntry);
            }
        }
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
}
