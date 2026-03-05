package org.ai4math.vandv.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FDRResults {
    private List<JsonNode> counterexamples;
    private boolean passed;
    private String assertionString;
    private List<FDRCounterexample> fdrCounterexamples;
    private List<JsonNode> errors;

    public void setAssertionString(String assertionString) {
        this.assertionString = assertionString;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isPassed() {
        return passed;
    }


    public List<FDRCounterexample> getFdrCounterexamples() {
        return fdrCounterexamples;
    }


    public String getAssertionString() {
        return assertionString;
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

    public void addCounterexamples(JsonNode counterexamples) {
        if (this.counterexamples == null) {
            this.counterexamples = new ArrayList<>(Arrays.asList(counterexamples));
        }
        else {
            this.counterexamples.add(counterexamples);
        }

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setType(counterexamples.get("type").asText());
        try {
            fdrCounterexample.setTrace(
                    new ObjectMapper()
                            .readerForListOf(String.class)
                            .readValue(counterexamples.get("implementation_behaviour").get("trace"))
            );
        } catch (IOException e){
            System.out.println("Error encountered converting the trace to a list: " + e.getMessage());
        }

        if (this.fdrCounterexamples == null) {
            this.fdrCounterexamples = new ArrayList<>(Arrays.asList(fdrCounterexample));
        }
        else {
            this.fdrCounterexamples.add(fdrCounterexample);
        }
    }

    public List<JsonNode> getCounterexamples() {
        return counterexamples;
    }


}
