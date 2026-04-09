package org.ai4math.vandv.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FDRCounterexampleTest {

    @Test
    public void givenValidEventMapAndTrace_whenConvertTraceToProcessesInvoked_thenProcessesTraceIsPopulated(){
        Map<String, String> eventMap = Map.of("1","Key","2","Trace","3","Value");
        List<String> expectedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        List<String> trace = List.of("2", "3", "1", "2", "2");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setTrace(trace);
        fdrCounterexample.convertTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getProcessesTrace());
        assertEquals(expectedProcessesTrace, fdrCounterexample.getProcessesTrace());
    }

    @Test
    public void givenValidEventMapAndEmptyTrace_whenConvertTraceToProcessesInvoked_thenProcessesTraceIsEmpty(){
        Map<String, String> eventMap = Map.of("1","Key","2","Trace","3","Value");
        List<String> expectedProcessesTrace = List.of();
        List<String> trace = List.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setTrace(trace);
        fdrCounterexample.convertTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getProcessesTrace());
        assertEquals(expectedProcessesTrace, fdrCounterexample.getProcessesTrace());

    }

    @Test
    public void givenEmptyEventMapAndValidTrace_whenConvertTraceToProcessesInvoked_thenProcessesTraceIsEmpty(){
        Map<String, String> eventMap = Map.of();
        List<String> expectedProcessesTrace = List.of();
        List<String> trace = List.of("2", "3", "1", "2", "2");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setTrace(trace);
        fdrCounterexample.convertTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getProcessesTrace());
        assertEquals(expectedProcessesTrace, fdrCounterexample.getProcessesTrace());

    }

    @Test
    public void givenEmptyEventMapAndEmptyTrace_whenConvertTraceToProcessesInvoked_thenProcessesTraceIsEmpty(){
        Map<String, String> eventMap = Map.of();
        List<String> expectedProcessesTrace = List.of();
        List<String> trace = List.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setTrace(trace);
        fdrCounterexample.convertTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getProcessesTrace());
        assertEquals(expectedProcessesTrace, fdrCounterexample.getProcessesTrace());
    }

    @Test
    public void givenValidEventMapAndRevealedTrace_whenConvertRevealedTraceToProcessesInvoked_thenRevealedProcessesTraceIsPopulated(){
        Map<String, String> eventMap = Map.of("1","Key","2","Trace","3","Value");
        List<String> expectedRevealedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        List<String> revealedTrace = List.of("2", "3", "1", "2", "2");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        fdrCounterexample.setProcessesTrace(processesTrace);

        fdrCounterexample.setRevealedTrace(revealedTrace);
        fdrCounterexample.convertRevealedTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getRevealedProcessesTrace());
        assertEquals(expectedRevealedProcessesTrace, fdrCounterexample.getRevealedProcessesTrace());
    }

    @Test
    public void givenValidEventMapAndEmptyRevealedTrace_whenConvertRevealedTraceToProcessesInvoked_thenRevealedProcessesTraceIsEmpty(){
        Map<String, String> eventMap = Map.of("1","Key","2","Trace","3","Value");
        List<String> expectedRevealedProcessesTrace = List.of();
        List<String> revealedTrace = List.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        fdrCounterexample.setProcessesTrace(processesTrace);

        fdrCounterexample.setRevealedTrace(revealedTrace);
        fdrCounterexample.convertRevealedTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getRevealedProcessesTrace());
        assertEquals(expectedRevealedProcessesTrace, fdrCounterexample.getRevealedProcessesTrace());
    }

    @Test
    public void givenEmptyEventMapAndValidRevealedTrace_whenConvertRevealedTraceToProcessesInvoked_thenRevealedProcessesTraceIsEmpty(){
        Map<String, String> eventMap = Map.of();
        List<String> expectedRevealedProcessesTrace = List.of();
        List<String> revealedTrace = List.of("2", "3", "1", "2", "2");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        fdrCounterexample.setProcessesTrace(processesTrace);

        fdrCounterexample.setRevealedTrace(revealedTrace);
        fdrCounterexample.convertRevealedTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getRevealedProcessesTrace());
        assertEquals(expectedRevealedProcessesTrace, fdrCounterexample.getRevealedProcessesTrace());
    }

    @Test
    public void givenEmptyEventMapAndEmptyRevealedTrace_whenConvertRevealedTraceToProcessesInvoked_thenRevealedProcessesTraceIsEmpty(){
        Map<String, String> eventMap = Map.of();
        List<String> expectedRevealedProcessesTrace = List.of();
        List<String> revealedTrace = List.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        fdrCounterexample.setProcessesTrace(processesTrace);

        fdrCounterexample.setRevealedTrace(revealedTrace);
        fdrCounterexample.convertRevealedTraceToProcesses(eventMap);

        assertNotNull(fdrCounterexample.getRevealedProcessesTrace());
        assertEquals(expectedRevealedProcessesTrace, fdrCounterexample.getRevealedProcessesTrace());
    }

    @Test
    public void givenEmptyProcessesTrace_whenSetNoTauTraceInvoked_thenNoTauTraceIsEmpty(){
        List<String> expectedNoTauTrace = List.of();
        List<String> processesTrace = List.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.setNoTauTrace();

        assertNotNull(fdrCounterexample.getNoTauTrace());
        assertEquals(expectedNoTauTrace, fdrCounterexample.getNoTauTrace());

    }

    @Test
    public void givenProcessesTraceWithTaus_whenSetNoTauTraceInvoked_thenNoTauTraceIsSubsetOfProcessesTrace(){
        List<String> expectedNoTauTrace = List.of("Trace", "Value", "Trace", "Trace");
        List<String> processesTrace = List.of("Trace", "Value", "τ", "Trace", "Trace");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.setNoTauTrace();

        assertNotNull(fdrCounterexample.getNoTauTrace());
        assertEquals(expectedNoTauTrace, fdrCounterexample.getNoTauTrace());
    }

    @Test
    public void givenProcessesTraceWithoutTaus_whenSetNoTauTraceInvoked_thenNoTauTraceIsProcessesTrace(){
        List<String> expectedNoTauTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        List<String> processesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.setNoTauTrace();

        assertNotNull(fdrCounterexample.getNoTauTrace());
        assertEquals(expectedNoTauTrace, fdrCounterexample.getNoTauTrace());
    }

    @Test
    public void givenEquivalentProcessesTraceAndRevealedTrace_whenAddHiddenInvoked_thenHiddenSetIsEmpty(){
        List<String> revealedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        List<String> processesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        Set<String> expectedHiddenSet = Set.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setRevealedProcessesTrace(revealedProcessesTrace);
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.addHidden();

        assertNotNull(fdrCounterexample.getHidden());
        assertEquals(expectedHiddenSet, fdrCounterexample.getHidden());
    }

    @Test
    public void givenDisparateProcessesTraceAndRevealedTrace_whenAddHiddenInvoked_thenHiddenSetIsDefined(){
        List<String> revealedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        Set<String> expectedHiddenSet = Set.of("Trace");

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setRevealedProcessesTrace(revealedProcessesTrace);
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.addHidden();

        assertNotNull(fdrCounterexample.getHidden());
        assertEquals(expectedHiddenSet, fdrCounterexample.getHidden());
    }

    @Test
    public void givenEmptyProcessesTraceAndRevealedTrace_whenAddHiddenInvoked_thenHiddenSetIsEmpty(){
        List<String> revealedProcessesTrace = List.of();
        List<String> processesTrace = List.of();
        Set<String> expectedHiddenSet = Set.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setRevealedProcessesTrace(revealedProcessesTrace);
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.addHidden();

        assertNotNull(fdrCounterexample.getHidden());
        assertEquals(expectedHiddenSet, fdrCounterexample.getHidden());
    }

    @Test
    public void givenNullProcessesTrace_whenAddHiddenInvoked_thenHiddenSetIsEmpty(){
        List<String> revealedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        List<String> processesTrace = null;
        Set<String> expectedHiddenSet = Set.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setRevealedProcessesTrace(revealedProcessesTrace);
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.addHidden();

        assertNotNull(fdrCounterexample.getHidden());
        assertEquals(expectedHiddenSet, fdrCounterexample.getHidden());
    }

    @Test
    public void givenNullRevealedTrace_whenAddHiddenInvoked_thenHiddenSetIsEmpty(){
        List<String> revealedProcessesTrace = null;
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        Set<String> expectedHiddenSet = Set.of();

        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setRevealedProcessesTrace(revealedProcessesTrace);
        fdrCounterexample.setProcessesTrace(processesTrace);
        fdrCounterexample.addHidden();

        assertNotNull(fdrCounterexample.getHidden());
        assertEquals(expectedHiddenSet, fdrCounterexample.getHidden());
    }

    @Test
    void givenType_whenSetAndGetType_thenReturnType(){
        String type = "TEST_TYPE";
        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setType(type);
        assertEquals(type, fdrCounterexample.getType());
    }

    @Test
    void givenRevealedTrace_whenSetAndGetRevealedTrace_thenReturnRevealedTrace(){
        List<String> revealedTrace = List.of("1", "4", "3", "1", "2");
        FDRCounterexample fdrCounterexample = new FDRCounterexample();
        fdrCounterexample.setRevealedTrace(revealedTrace);
        assertEquals(revealedTrace, fdrCounterexample.getRevealedTrace());
    }
}
