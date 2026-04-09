package org.ai4math.vandv.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FDRResultsTest {
    private static JsonNode validJson;
    private static JsonNode malformedJson;
    private static JsonNode emptyJson;
    private static JsonNode nullJson;
    private static List<String> trace;
    private static List<String> revealedTrace;
    private static final String type = "deadlock assertion";

    @BeforeEach
    void initialiseTests(){
        ObjectMapper mapper = new ObjectMapper();

        // Initialising FDR Results for testing
        trace = List.of("2", "3", "1", "2", "2");
        revealedTrace = List.of("2", "3", "4", "2", "2");

        ObjectNode impl = mapper.createObjectNode();
        ArrayNode arrayNode = impl.putArray("trace");
        for (String item : trace) {
            arrayNode.add(item);
        }
        ArrayNode revArrayNode = impl.putArray("revealed_trace");
        for (String item : revealedTrace) {
            revArrayNode.add(item);
        }

        ObjectNode counterexample = mapper.createObjectNode();
        counterexample.put("type", type);
        counterexample.set("implementation_behaviour", impl);
        validJson=counterexample;

        ObjectNode malfImpl = mapper.createObjectNode();
        ArrayNode malfNode = malfImpl.putArray("trace");
        for (String item : trace) {
            malfNode.add(item);
        }

        ObjectNode malformedCounterexample = mapper.createObjectNode();
        malformedCounterexample.put("type", "deadlock assertion");
        malformedCounterexample.set("implementation_behaviour", malfImpl);
        malformedJson=malformedCounterexample;

        ObjectNode emptyImpl = mapper.createObjectNode();
        emptyImpl.putArray("trace");
        emptyImpl.putArray("revealed_trace");

        ObjectNode emptyCounterexample = mapper.createObjectNode();
        emptyCounterexample.put("type", "deadlock assertion");
        emptyCounterexample.set("implementation_behaviour", emptyImpl);

        emptyJson = emptyCounterexample;
    }

    @Test
    void givenNullErrors_whenAddErrors_thenErrorsAreEmpty(){
        FDRResults fdrResults = new FDRResults();
        fdrResults.addError(nullJson);

        assertEquals(0, fdrResults.getErrors().size());
    }

    @Test
    void givenErrors_whenAddErrors_thenErrorsArePopulated(){
        FDRResults fdrResults = new FDRResults();
        fdrResults.addError(emptyJson);

        assertEquals(1, fdrResults.getErrors().size());
        assertEquals(emptyJson, fdrResults.getErrors().getFirst());
    }

    @Test
    void givenMultipleErrors_whenAddErrors_thenErrorsArePopulated(){
        FDRResults fdrResults = new FDRResults();
        fdrResults.addError(emptyJson);
        fdrResults.addError(emptyJson);

        assertEquals(2, fdrResults.getErrors().size());
        assertEquals(emptyJson, fdrResults.getErrors().getFirst());
    }

    @Test
    void givenValidCounterExample_whenAddCounterexamplesInvoked_thenPopulateTraces(){
        FDRResults fdrResults = new FDRResults();
        fdrResults.addCounterexamples(validJson);

        assertEquals(validJson, fdrResults.getCounterexamples().getFirst());
        assertEquals(type, fdrResults.getFdrCounterexamples().getFirst().getType());
        assertEquals(trace, fdrResults.getFdrCounterexamples().getFirst().getTrace());
        assertEquals(revealedTrace, fdrResults.getFdrCounterexamples().getFirst().getRevealedTrace());
    }

    @Test
    void givenMalformedCounterExample_whenAddCounterexamplesInvoked_thenPopulateTraces(){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        FDRResults fdrResults = new FDRResults();
        fdrResults.addCounterexamples(malformedJson);

        assertTrue(
                outContent.toString()
                        .startsWith("Error encountered converting the trace to a list: "),
                "Expected the stream to end with given message but was "+outContent.toString());


        assertEquals(malformedJson, fdrResults.getCounterexamples().getFirst());
        assertEquals(type, fdrResults.getFdrCounterexamples().getFirst().getType());
        assertEquals(trace, fdrResults.getFdrCounterexamples().getFirst().getTrace());
        assertNull(fdrResults.getFdrCounterexamples().getFirst().getRevealedTrace());
    }

    @Test
    void givenEmptyCounterExamples_whenAddCounterexamplesInvoked_thenPopulateTraces(){
        FDRResults fdrResults = new FDRResults();
        fdrResults.addCounterexamples(emptyJson);

        assertEquals(emptyJson, fdrResults.getCounterexamples().getFirst());
        assertEquals(type, fdrResults.getFdrCounterexamples().getFirst().getType());
        assertEquals(0, fdrResults.getFdrCounterexamples().getFirst().getTrace().size());
        assertEquals(0, fdrResults.getFdrCounterexamples().getFirst().getRevealedTrace().size());
    }

    @Test
    void givenMultipleCounterExamples_whenAddCounterexamplesInvoked_thenPopulateTraces(){
        FDRResults fdrResults = new FDRResults();
        fdrResults.addCounterexamples(validJson);
        fdrResults.addCounterexamples(emptyJson);

        JsonNode validJsonCounter = fdrResults.getCounterexamples().getFirst();
        FDRCounterexample validCounter = fdrResults.getFdrCounterexamples().getFirst();
        JsonNode emptyJsonCounter = fdrResults.getCounterexamples().get(1);
        FDRCounterexample emptyCounter = fdrResults.getFdrCounterexamples().get(1);

        assertEquals(validJson, validJsonCounter);
        assertEquals(type, validCounter.getType());
        assertEquals(trace, validCounter.getTrace());
        assertEquals(revealedTrace, validCounter.getRevealedTrace());
        assertEquals(emptyJson, emptyJsonCounter);
        assertEquals(type, emptyCounter.getType());
        assertEquals(0, emptyCounter.getTrace().size());
        assertEquals(0, emptyCounter.getRevealedTrace().size());
    }
}
