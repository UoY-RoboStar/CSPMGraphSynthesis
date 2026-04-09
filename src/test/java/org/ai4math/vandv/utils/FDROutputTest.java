package org.ai4math.vandv.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FDROutputTest {

    private static FDRResults validFdrResults;
    private static FDRResults emptyFdrResults;
    private static FDRResults passingFdrResults;
    private static FDRResults errorFdrResults;
    private static FDRResults nullFdrResults;
    private static JsonNode validEventMap;
    private static JsonNode emptyJson;
    private static JsonNode nullJson;
    private static Map<String, String> parsedEventmap;
    private static Map<String, String> parsedEmptyEventMap;
    private static List<String> processesTrace;
    private static List<String> revealedProcessesTrace;

    @BeforeEach
    void initialiseTests(){
        // Initialising event maps for testing
        ObjectMapper mapper = new ObjectMapper();
        parsedEventmap = Map.of("1","τ","2","Trace","3","Value", "4", "Key");
        validEventMap = mapper.valueToTree(parsedEventmap);

        parsedEmptyEventMap = Map.of();
        emptyJson = mapper.valueToTree(parsedEmptyEventMap);

        // Initialising FDR Results for testing
        List<String> traceList = List.of("2", "3", "1", "2", "2");
        List<String> revealedTraceList = List.of("2", "3", "4", "2", "2");

        ObjectNode impl = mapper.createObjectNode();
        ArrayNode arrayNode = impl.putArray("trace");
        for (String item : traceList) {
            arrayNode.add(item);
        }
        arrayNode = impl.putArray("revealed_trace");
        for (String item : revealedTraceList) {
            arrayNode.add(item);
        }

        ObjectNode counterexample = mapper.createObjectNode();
        counterexample.put("type", "deadlock assertion");
        counterexample.set("implementation_behaviour", impl);

        validFdrResults = new FDRResults();
        validFdrResults.addCounterexamples(counterexample);
        validFdrResults.setPassed(false);

        ObjectNode emptyImpl = mapper.createObjectNode();
        emptyImpl.putArray("trace");
        emptyImpl.putArray("revealed_trace");

        ObjectNode emptyCounterexample = mapper.createObjectNode();
        emptyCounterexample.put("type", "deadlock assertion");
        emptyCounterexample.set("implementation_behaviour", emptyImpl);

        emptyFdrResults = new FDRResults();
        emptyFdrResults.addCounterexamples(emptyCounterexample);
        emptyFdrResults.setPassed(false);

        passingFdrResults = new FDRResults();
        passingFdrResults.setPassed(true);

        errorFdrResults = new FDRResults();
        errorFdrResults.addCounterexamples(counterexample);
        errorFdrResults.addError(validEventMap);


        // Expected traces for testing
        processesTrace = List.of("Trace", "Value", "τ", "Trace", "Trace");
        revealedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
    }

    @Test
    void givenNullWarnings_whenAddWarnings_thenWarningsAreEmpty(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addWarnings(nullJson);

        assertEquals(0, fdrOutput.getWarnings().size());
    }

    @Test
    void givenWarnings_whenAddWarnings_thenWarningsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addWarnings(emptyJson);

        assertEquals(1, fdrOutput.getWarnings().size());
        assertEquals(emptyJson, fdrOutput.getWarnings().getFirst());
    }

    @Test
    void givenMultipleWarnings_whenAddWarnings_thenWarningsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addWarnings(emptyJson);
        fdrOutput.addWarnings(emptyJson);

        assertEquals(2, fdrOutput.getWarnings().size());
        assertEquals(emptyJson, fdrOutput.getWarnings().getFirst());
    }

    @Test
    void givenNullErrors_whenAddErrors_thenErrorsAreEmpty(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addError(nullJson);

        assertEquals(0, fdrOutput.getErrors().size());
    }

    @Test
    void givenErrors_whenAddErrors_thenErrorsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addError(emptyJson);

        assertEquals(1, fdrOutput.getErrors().size());
        assertEquals(emptyJson, fdrOutput.getErrors().getFirst());
    }

    @Test
    void givenMultipleErrors_whenAddErrors_thenErrorsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addError(emptyJson);
        fdrOutput.addError(emptyJson);

        assertEquals(2, fdrOutput.getErrors().size());
        assertEquals(emptyJson, fdrOutput.getErrors().getFirst());
    }

    @Test
    void givenNullResults_whenAddResults_thenResultsAreEmpty(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addResults(nullJson);

        assertEquals(0, fdrOutput.getResults().size());
    }

    @Test
    void givenResults_whenAddResults_thenResultsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addResults(emptyJson);

        assertEquals(1, fdrOutput.getResults().size());
        assertEquals(emptyJson, fdrOutput.getResults().getFirst());
    }

    @Test
    void givenMultipleResults_whenAddResults_thenResultsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addResults(emptyJson);
        fdrOutput.addResults(validEventMap);

        assertEquals(2, fdrOutput.getResults().size());
        assertEquals(emptyJson, fdrOutput.getResults().getFirst());
        assertEquals(validEventMap, fdrOutput.getResults().get(1));
    }

    @Test
    void givenNullFdrResults_whenAddFdrResults_thenFdrResultsAreEmpty(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addFdrResults(nullFdrResults);

        assertEquals(0, fdrOutput.getFdrResults().size());
    }

    @Test
    void givenFdrResults_whenAddFdrResults_thenFdrResultsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addFdrResults(emptyFdrResults);

        assertEquals(1, fdrOutput.getFdrResults().size());
        assertEquals(emptyFdrResults, fdrOutput.getFdrResults().getFirst());
    }

    @Test
    void givenMultipleFdrResults_whenAddFdrResults_thenFdrResultsArePopulated(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.addFdrResults(emptyFdrResults);
        fdrOutput.addFdrResults(validFdrResults);

        assertEquals(2, fdrOutput.getFdrResults().size());
        assertEquals(emptyFdrResults, fdrOutput.getFdrResults().getFirst());
        assertEquals(validFdrResults, fdrOutput.getFdrResults().get(1));
    }

    @Test
    void givenValidEventMap_whenParseEventMapInvoked_thenParseEventMap(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.parseEventMap(validEventMap);

        assertEquals(parsedEventmap, fdrOutput.getEventMapParsed());
    }

    @Test
    void givenEmptyEventMap_whenParseEventMapInvoked_thenParseEventMap(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.parseEventMap(emptyJson);

        assertEquals(0, fdrOutput.getEventMapParsed().size());
    }

    @Test
    void givenNullEventMap_whenParseEventMapInvoked_thenDoNotParseEventMap(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.parseEventMap(nullJson);

        assertNull(fdrOutput.getEventMapParsed());
    }

    @Test
    void givenValidEventMapAndResults_whenTransformCounterExamplesInvoked_thenParseEventMap(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.setEventMap(validEventMap);
        fdrOutput.addFdrResults(validFdrResults);
        fdrOutput.transformCounterexamples();

        assertEquals(parsedEventmap, fdrOutput.getEventMapParsed());
    }

    @Test
    void givenValidEventMapAndResults_whenTransformCounterExamplesInvoked_thenPopulateProcessTraces(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.setEventMap(validEventMap);
        fdrOutput.addFdrResults(validFdrResults);
        fdrOutput.transformCounterexamples();

        FDRResults fdrResults = fdrOutput.getFdrResults().getFirst();
        FDRCounterexample counterexample = fdrResults.getFdrCounterexamples().getFirst();
        assertFalse(fdrResults.isPassed());
        assertEquals(processesTrace, counterexample.getProcessesTrace());
        assertEquals(revealedProcessesTrace, counterexample.getRevealedProcessesTrace());
    }

    @Test
    void givenNullResults_whenTransformCounterExamplesInvoked_thenDoNotPopulateCounterexamples(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.setEventMap(validEventMap);
        fdrOutput.addFdrResults(nullFdrResults);
        fdrOutput.transformCounterexamples();

        assertEquals(0,fdrOutput.getFdrResults().size());
    }

    @Test
    void givenEmptyResults_whenTransformCounterExamplesInvoked_thenPopulateEmptyTraces(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.setEventMap(validEventMap);
        fdrOutput.addFdrResults(emptyFdrResults);
        fdrOutput.transformCounterexamples();

        FDRResults fdrResults = fdrOutput.getFdrResults().getFirst();
        FDRCounterexample counterexample = fdrResults.getFdrCounterexamples().getFirst();
        assertFalse(fdrResults.isPassed());
        assertEquals(List.of(), counterexample.getProcessesTrace());
        assertEquals(List.of(), counterexample.getRevealedProcessesTrace());
    }


    @Test
    void givenResultsWithErrors_whenTransformCounterExamplesInvoked_thenDoNotPopulateCounterexamples(){
        FDROutput fdrOutput = new FDROutput();
        fdrOutput.setEventMap(validEventMap);
        fdrOutput.addFdrResults(errorFdrResults);
        fdrOutput.transformCounterexamples();

        FDRResults fdrResults = fdrOutput.getFdrResults().getFirst();
        assertFalse(fdrResults.isPassed());
        assertNull(fdrResults.getFdrCounterexamples().getFirst().getProcessesTrace());
        assertNull(fdrResults.getFdrCounterexamples().getFirst().getRevealedProcessesTrace());
        assertNotNull(fdrResults.getErrors());
    }
}
