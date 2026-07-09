package org.ai4math.vandv;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FDRInvocation {
    private static final String FDR_COMMAND = "refines";
    private static final String TAUS = "--reveal-taus";
    private static final String FORMAT = "--format=json";
    private static final String QUIET = "--q";
    private static final String REUSE = "--compiler-reuse-machines=off";

    private FDROutput fdrOutput;
    private String filepath;
    private String rerunFilePath;
    private int count;

    public FDRInvocation(){}

    public void performVerification(String filepath, int count) {
        performVerification(filepath, count, false);
    }

    public void performVerification(String filepath, int count, boolean rerun){
        this.filepath = filepath;
        this.count = count;
        System.out.println("Running FDR on file "+count+": " + filepath );
        ProcessBuilder PB = new ProcessBuilder(FDR_COMMAND, filepath, FORMAT, REUSE, TAUS, QUIET);
        if (rerun){
            PB = new ProcessBuilder(FDR_COMMAND, this.rerunFilePath, FORMAT, REUSE, TAUS, QUIET);
        }
        Process process = null;
        fdrOutput = new FDROutput();

        try {
            process = PB.start();
            if (!process.waitFor(8, TimeUnit.SECONDS)){
                reportError("Operation timed out");
                process.destroy(); // Equivalent to process.terminate()
            } else {

                String stdout = readStream(process.getInputStream());
                String stderr = readStream(process.getErrorStream());

                int exitCode = process.waitFor();
                process.destroy(); // Equivalent to process.terminate()
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
        } finally {
            if (process != null) {
                process.destroy(); // Equivalent to process.terminate()
            }
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

        List<String> tickProcesses = fdrOutput.checkForTicks();
        if (!tickProcesses.isEmpty()) {
            rerunVerification(tickProcesses);
        }
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

    private void rerunVerification(List<String> rerunProcesses) {
        this.rerunFilePath = filepath.substring(0,filepath.indexOf("."))+"tempVerificationRerun.csp";

        String cspContent;

        try (BufferedReader br = new BufferedReader(new FileReader(this.filepath))) {
            cspContent = br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            System.err.println("Exception occurred when reading file for rerun of verification: " + e.getMessage());
            throw new RuntimeException(e);
        }

        cspContent = replaceProcessAssertions(cspContent, rerunProcesses);

        File file = new File(this.rerunFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(cspContent);
        } catch (Exception e) {
            System.err.println("Exception occurred when writing temp file for rerun of verification: " + e.getMessage());
            throw new RuntimeException(e);
        }

        performVerification(this.filepath, this.count, true);

        file.delete();
    }

    private String replaceProcessAssertions(String csp, List<String> rerunProcesses){
        for (String process : rerunProcesses) {
            int loc = csp.indexOf("assert "+process);
            String endString = csp.substring(loc);
            String assertionString = endString.contains("\n")?endString.substring(0, endString.indexOf("\n")):endString;
            String endAssertion = assertionString.substring(assertionString.indexOf(":"));
            csp = csp.replace(assertionString, "assert "+process+"; RUN({"+getRandomChannel(csp)+"}) "+endAssertion);
        }

        return csp;
    }

    private String getRandomChannel(String csp){
        String channels = csp.substring(csp.indexOf("channel "));
        String channel = channels.substring(0, channels.indexOf("\n"));
        String[] channelParts = channel.split(" : ");
        return channelParts[0].replace("channel ", "");
    }
}