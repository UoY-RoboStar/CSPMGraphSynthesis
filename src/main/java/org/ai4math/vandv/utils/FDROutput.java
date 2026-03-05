package org.ai4math.vandv.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        if (this.results == null) {
            this.results = new ArrayList<>(Arrays.asList(results));
        }
        else {
            this.results.add(results);
        }
    }

    public void addWarnings(JsonNode warnings) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<>(Arrays.asList(warnings));
        }
        else {
            this.warnings.add(warnings);
        }
    }

    public void addError(JsonNode errors) {
        if (this.errors == null) {
            this.errors = new ArrayList<>(Arrays.asList(errors));
        }
        else {
            this.errors.add(errors);
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
        if (this.fdrResults == null) {
            this.fdrResults = new ArrayList<>(Arrays.asList(fdrResults));
        }
        else {
            this.fdrResults.add(fdrResults);
        }
    }

    public JsonNode getPrintStatementResults() {
        return printStatementResults;
    }

    public JsonNode getEventMap() {
        return eventMap;
    }

    public void setPrintStatementResults(JsonNode printStatementResults) {
        this.printStatementResults = printStatementResults;
    }

    public void setEventMap(JsonNode eventMap) {
        this.eventMap = eventMap;
    }

    private void parseEventMap(JsonNode eventMap){
        TypeReference<Map<String, String>> typeReferenceMap = new TypeReference<Map<String, String>>() {};
        if (eventMap != null) {
            try {
                Map<String, String> parsedEventMap = new ObjectMapper().readValue(eventMap.traverse(), typeReferenceMap);
                this.eventMapParsed = parsedEventMap;//.entrySet()
                        //.stream()
                        //.filter(e -> e.getKey() != null)
                        //.collect(
                        //        Collectors.toMap(
                       //                 e -> Integer.valueOf(e.getKey()),
                       //                 Map.Entry::getValue
                        //        )
                        //);
            } catch (IOException e) {
                System.out.println("Error encountered parsing the eventMap: " + e.getMessage());
            }
        }
    }

    public void transformCounterexamples(){
        parseEventMap(this.eventMap);
        if (eventMap != null) {
            for (FDRResults fdrResult : this.fdrResults) {
                if (!fdrResult.isPassed()) {
                    for (FDRCounterexample counterexamples : fdrResult.getFdrCounterexamples()) {
                        counterexamples.convertTraceToProcesses(this.eventMapParsed);
                    }
                }
            }
        }
    }
}
