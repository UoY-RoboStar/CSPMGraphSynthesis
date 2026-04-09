package org.ai4math.vandv;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FDRInvocation {
    private static final String FDR_COMMAND = "refines";
    private static final String TAUS = "--reveal-taus";
    private static final String FORMAT = "--format=json";
    private static final String QUIET = "--q";

    private FDROutput fdrOutput;

    public FDRInvocation(){}

    public void performVerification(String filepath){
        System.out.println("Running FDR on " + filepath);
        ProcessBuilder PB = new ProcessBuilder(FDR_COMMAND, filepath, FORMAT, QUIET, TAUS);
        Process process = null;
        fdrOutput = new FDROutput();

        try {
            process = PB.start();
            if (!process.waitFor(3, TimeUnit.SECONDS)){
                reportError("Operation timed out with ");
            } else {

                String stdout = readStream(process.getInputStream());
                String stderr = readStream(process.getErrorStream());

                int exitCode = process.waitFor();
                System.out.println("Finished");

                if (exitCode == 0) {
                    if (!stderr.isEmpty()) {
                        // FDR does not output errors to an error stream
                        System.out.println("Log data was:");
                        System.out.println(stderr);
                        reportError(stderr);
                        return;
                    }

                    // Parse the JSON data
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode parsedData = mapper.readTree(stdout);
                    parseData(parsedData);

                } else {
                    System.out.println("Failed - exit code " + exitCode);
                    reportError("Failed - exit code " + exitCode);
                }
            }

        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Process interrupted.");
            if (process != null) {
                process.destroy(); // Equivalent to process.terminate()
            }
            Thread.currentThread().interrupt();
        } catch (Exception e){
            System.err.println("Unexpected error occurred: " + e.getClass().getName() + " : " + e.getMessage());
        }
    }

    private void reportError(String message){
        JsonNode errorNode = JsonNodeFactory.instance.textNode(message);
        fdrOutput.addError(errorNode);
    }

    public FDROutput getFdrOutput() {
        return fdrOutput;
    }

    // Helper method to read an InputStream into a String
    private String readStream(InputStream is) throws IOException {
        if (is.available()>0) {
            return new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
        }
        return "";
    }

    private void parseData(JsonNode parsedData){
        parseErrors(parsedData);
        parseWarnings(parsedData);

        // Parse the Results
        parseResults(parsedData);

        fdrOutput.setPrintStatementResults(parsedData.get("print_statement_results"));
        fdrOutput.setEventMap(parsedData.get("event_map"));
        fdrOutput.transformCounterexamples();
    }

    private void parseErrors(JsonNode parsedData){
        // Print Global Errors
        if (parsedData.has("errors")) {
            for (JsonNode error : parsedData.get("errors")) {
                System.out.println("Error: " + error.asText());
                fdrOutput.addError(error);
            }
        }
    }

    private void parseWarnings(JsonNode parsedData){
        // Print Global Warnings
        if (parsedData.has("warnings")) {
            for (JsonNode warning : parsedData.get("warnings")) {
                System.out.println("Warning: " + warning.asText());
                fdrOutput.addWarnings(warning);
            }
        }
    }

    private void parseResults(JsonNode parsedData){
        if (parsedData.has("results")) {
            for (JsonNode assertion : parsedData.get("results")) {
                FDRResults fdrResults = new FDRResults();

                String assertionString = assertion.get("assertion_string").asText();
                System.out.println("Assertion: " + assertionString);
                fdrResults.setAssertionString(assertionString);
                fdrOutput.addResults(assertion);

                if (assertion.has("errors") && !assertion.get("errors").isEmpty()) {
                    System.out.println("    Errors during assertion");
                    for (JsonNode error : assertion.get("errors")) {
                        System.out.println("    Error: " + error.asText());
                        fdrResults.addError(error);
                    }
                    fdrResults.setPassed(false);
                } else {
                    System.out.println("    Visited States: " + assertion.get("visited_states").asInt());
                    boolean passed = assertion.get("result").asInt() == 1;
                    System.out.println("    Passed: " + passed);
                    fdrResults.setPassed(passed);
                    if (assertion.has("counterexamples")) {
                        for (JsonNode ce : assertion.get("counterexamples")) {
                            fdrResults.addCounterexamples(ce);
                        }
                    }
                }

                fdrOutput.addFdrResults(fdrResults);
            }
        }
    }
}