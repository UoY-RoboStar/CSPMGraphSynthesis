package org.ai4math.vandv;

import com.fasterxml.jackson.databind.JsonNode;
import org.ai4math.vandv.utils.FDRCounterexample;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@net.jcip.annotations.NotThreadSafe
public class FDRInvocationTest {

    // Integration Tests
    @Test
    void givenValidDeadlockFreeCSP_whenPerformVerificationInvoked_thenOutputWithoutTraceFormed() {
        FDRInvocation fdrInvocation = new FDRInvocation();
        fdrInvocation.performVerification(getResourcePath("DeadlockFreeCSP.csp"));

        FDROutput fdrOutput = fdrInvocation.getFdrOutput();
        List<FDRResults> fdrResults = fdrOutput.getFdrResults();
        for (FDRResults fdrResult : fdrResults) {
            assertTrue(fdrResult.isPassed(), "assertion did not pass");
            List<FDRCounterexample> counterexamples = fdrResult.getFdrCounterexamples();
            assertNull(counterexamples,
                        "trace not null but: " + counterexamples);
        }

    }


    @Test
    void givenValidDeadlockedCSP_whenPerformVerificationInvoked_thenOutputWithoutTraceFormed() {
        {
            FDRInvocation fdrInvocation = new FDRInvocation();
            fdrInvocation.performVerification(getResourcePath("DeadlockedCSP.csp"));

            List<String> expectedTrace = new ArrayList<>(Arrays.asList("secondTestingChannel", "testingChannel"));

            FDROutput fdrOutput = fdrInvocation.getFdrOutput();
            List<FDRResults> fdrResults = fdrOutput.getFdrResults();
            for (FDRResults fdrResult : fdrResults) {
                assertFalse(fdrResult.isPassed(), "assertion passed");
                List<FDRCounterexample> counterexamples = fdrResult.getFdrCounterexamples();
                for (FDRCounterexample counterexample : counterexamples) {
                    assertNotNull(counterexample.getProcessesTrace(),
                            "trace is null");
                    assertEquals(expectedTrace,counterexample.getProcessesTrace(),
                            "trace does not match the expectation of "+expectedTrace+": "
                                    + counterexample.getProcessesTrace());
                }
            }
        }
    }

    @Test
    void givenValidImmediatelyDeadlockedCSP_whenPerformVerificationInvoked_thenOutputWithoutTraceFormed() {
        {
            FDRInvocation fdrInvocation = new FDRInvocation();
            fdrInvocation.performVerification(getResourcePath("Deadlock.csp"));

            List<String> expectedTrace = new ArrayList<>(List.of());

            FDROutput fdrOutput = fdrInvocation.getFdrOutput();
            List<FDRResults> fdrResults = fdrOutput.getFdrResults();
            for (FDRResults fdrResult : fdrResults) {
                assertFalse(fdrResult.isPassed(), "assertion passed");
                List<FDRCounterexample> counterexamples = fdrResult.getFdrCounterexamples();
                for (FDRCounterexample counterexample : counterexamples) {
                    assertNotNull(counterexample.getProcessesTrace(),
                            "processes trace is null");
                    assertEquals(expectedTrace,counterexample.getProcessesTrace(),
                            "nonempty processes trace provided: "
                                    + counterexample.getProcessesTrace());
                }
            }
        }
    }

    @Test
    void givenInvalidCSP_whenPerformVerificationInvoked_thenOutputWithErrors() {
        FDRInvocation fdrInvocation = new FDRInvocation();
        fdrInvocation.performVerification(getResourcePath("Invalid.csp"));

        FDROutput fdrOutput = fdrInvocation.getFdrOutput();
        List<FDRResults> fdrResults = fdrOutput.getFdrResults();
        assertNull(fdrResults, "Results weren't null");
        List<JsonNode> errors = fdrOutput.getErrors();

        String expectedError = "Invalid.csp:4:42-46:\n    test is not in scope";
        String e = errors.getFirst().asText();
        String expected = e.substring(e.length()-expectedError.length());
        System.out.println(expected);
        assertTrue(expected.matches(expectedError), "Unexpected error message");

        assertEquals(1, errors.size(), "More than one error");
    }

    @Test
    void givenValidCSPButInvalidCSPM_whenPerformVerificationInvoked_thenOutputWithErrors() {
        FDRInvocation fdrInvocation = new FDRInvocation();
        fdrInvocation.performVerification(getResourcePath("InvalidCSPM.csp"));

        String expectedError = "An operator that cannot be recursed through was recursed through. " +
                "In particular, the process:\n    TestingProcess\ncontains a recursion through the process:\n    " +
                "secondTestingChannel -> testingChannel -> SeqProcess ; testingChannel -> STOP\n" +
                "However, the operator  ; does not allow recursion.\nOperator Stack:\n    TestingProcess:\n        " +
                "secondTestingChannel -> testingChannel -> SeqProcess ; testingChannel -> STOP\n";

        FDROutput fdrOutput = fdrInvocation.getFdrOutput();
        List<FDRResults> fdrResults = fdrOutput.getFdrResults();
        assertNotNull(fdrResults, "Results were null");
        assertNull(fdrResults.getFirst().getFdrCounterexamples(), "Counterexamples not null");
        assertFalse(fdrResults.getFirst().isPassed(), "Assertion passed");
        List<JsonNode> errors = fdrResults.getFirst().getErrors();
        assertEquals(expectedError, errors.getFirst().asText(), "Unexpected error message");
    }

    @Test
    void givenInvalidFilePath_whenPerformVerificationInvoked_thenErrorAddedToOutput(){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        FDRInvocation fdrInvocation = new FDRInvocation();
        fdrInvocation.performVerification("InvalidCSPM.csp");

        assertTrue(
                outContent.toString()
                        .endsWith("Error: <unknown location>: Could not open the file 'InvalidCSPM.csp'"
                                +System.getProperty("line.separator")),
                "Expected the stream to end with given message but was "+outContent.toString());

        FDROutput fdrOutput = fdrInvocation.getFdrOutput();
        assertEquals(1, fdrOutput.getErrors().size());
        assertEquals("<unknown location>: Could not open the file 'InvalidCSPM.csp'",
                fdrOutput.getErrors().getFirst().asText());

        assertNull(fdrInvocation.getFdrOutput().getFdrResults());
    }


    public String getResourcePath(String resourceName){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        return file.getAbsolutePath();
    }
}
