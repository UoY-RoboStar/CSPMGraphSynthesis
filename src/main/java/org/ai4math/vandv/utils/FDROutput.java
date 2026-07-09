package org.ai4math.vandv.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ai4math.cspm.Keywords;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FDROutput {
    private List<JsonNode> errors;
    private List<JsonNode> warnings;
    private List<JsonNode> results;
    private List<FDRResults> fdrResults;
    private JsonNode printStatementResults;
    private JsonNode eventMap;
    private Map<String, String> eventMapParsed;

    public FDROutput(){
    }

    public void addResults(JsonNode results) {
        if (this.results == null && results!=null) {
            this.results = new ArrayList<>(List.of(results));
        }
        else if (results != null) {
            this.results.add(results);
        } else {
            this.results = List.of();
        }
    }

    public void addWarnings(JsonNode warnings) {
        if (this.warnings == null && warnings!=null) {
            this.warnings = new ArrayList<>(List.of(warnings));
        }
        else if (warnings != null) {
            this.warnings.add(warnings);
        }
        else {
            this.warnings = List.of();
        }
    }

    public void addError(JsonNode errors) {
        if (this.errors == null && errors!=null) {
            this.errors = new ArrayList<>(List.of(errors));
        }
        else if (errors != null) {
            this.errors.add(errors);
        }
        else {
            this.errors = List.of();
        }
    }

    public List<JsonNode> getErrors() {
        return errors;
    }

    public List<JsonNode> getResults() {
        return results;
    }

    public List<JsonNode> getWarnings() {
        return warnings;
    }

    public List<FDRResults> getFdrResults() {
        return fdrResults;
    }

    public void setFdrResults(List<FDRResults> fdrResults) {
        this.fdrResults = fdrResults;
    }

    public void addFdrResults(FDRResults fdrResults){
        if (this.fdrResults == null && fdrResults!=null) {
            this.fdrResults = new ArrayList<>(List.of(fdrResults));
        }
        else if (fdrResults != null) {
            this.fdrResults.add(fdrResults);
        } else {
            this.fdrResults = List.of();
        }
    }

    public JsonNode getPrintStatementResults() {
        return printStatementResults;
    }

    public JsonNode getEventMap() {
        return eventMap;
    }

    public Map<String, String> getEventMapParsed() {
        return eventMapParsed;
    }

    public void setPrintStatementResults(JsonNode printStatementResults) {
        this.printStatementResults = printStatementResults;
    }

    public void setEventMap(JsonNode eventMap) {
        this.eventMap = eventMap;
    }

    public void parseEventMap(JsonNode eventMap){
        TypeReference<Map<String, String>> typeReferenceMap = new TypeReference<Map<String, String>>() {};
        if (eventMap != null) {
            try {
                this.eventMapParsed = new ObjectMapper().readValue(eventMap.traverse(), typeReferenceMap);
            } catch (IOException e) {
                System.out.println("Error encountered parsing the eventMap: " + e.getMessage());
            }
        }
    }

    public void transformCounterexamples(){
        parseEventMap(this.eventMap);
        if (this.eventMap != null && this.fdrResults != null) {
            for (FDRResults fdrResult : this.fdrResults) {
                if (!fdrResult.isPassed() && fdrResult.getErrors()==null) {
                    for (FDRCounterexample counterexamples : fdrResult.getFdrCounterexamples()) {
                        counterexamples.convertTraceToProcesses(this.eventMapParsed);
                        counterexamples.convertRevealedTraceToProcesses(this.eventMapParsed);
                    }
                }
            }
        }
    }

    public List<String> checkForTicks() {
        List<String> rerunProcesses = new ArrayList<>();
        for (FDRResults fdrResult : this.fdrResults) {
            if (!fdrResult.isPassed() && fdrResult.getErrors() == null) {
                for (FDRCounterexample counterexamples : fdrResult.getFdrCounterexamples()) {
                    if (counterexamples.getRevealedProcessesTrace().contains(Keywords.TICK)){
                        rerunProcesses.add(fdrResult.getAssertionString().split(" :")[0]);
                        break;
                    }
                }
            }
        }
        return rerunProcesses;
    }
}
