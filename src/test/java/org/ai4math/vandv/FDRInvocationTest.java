package org.ai4math.vandv;

import org.ai4math.vandv.utils.FDRCounterexample;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

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
    void givenInvalidDeadlockFreeCSP_whenPerformVerificationInvoked_thenOutputWithErrors() {

    }

    public String getResourcePath(String resourceName){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        return file.getAbsolutePath();
    }
}
