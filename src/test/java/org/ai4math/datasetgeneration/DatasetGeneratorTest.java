package org.ai4math.datasetgeneration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.jcip.annotations.NotThreadSafe;
import org.ai4math.vandv.utils.FDRCounterexample;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@NotThreadSafe
public class DatasetGeneratorTest {

    public static String resourcePath;
    public static String datasetPath;
    public static String errorPath;
    public static String filePath;
    public static String errorFilePath;
    public static FDROutput fdrOutput;
    public static FDROutput fdrTickOutput;
    public static FDROutput fdrErrorOutput;
    public static FDROutput fdrRecursionErrorOutput;
    public static FDROutput fdrLargeErrorOutput;
    public static FDROutput fdrVerErrorOutput;
    public static FDROutput fdrFailureOutput;
    public static FDROutput fdrTauOutput;
    public static String largeError;
    public static String type = "testAssert :[deadlock free]";
    private static String processesTraceString;
    private static String processesTickTraceString;
    private static String revealedProcessesTraceString;
    private static String revealedTickProcessesTraceString;
    private static String revealedTauProcessesTraceString;
    private static String noTauTraceString;
    private static String noTauTickTraceString;
    private static String hiddenString;
    private static String headerText;
    private static String errorHeaderText;

    @BeforeAll
    public static void SetUp() throws IOException {
        resourcePath = System.getProperty("user.home");
        datasetPath = "datasetGeneratorTest.csv";
        errorPath = "Error.csv";
        Path dir = Paths.get(resourcePath, "test", "generator");
        resourcePath = dir.toAbsolutePath().toString();
        filePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", datasetPath).toString();
        errorFilePath = Paths.get(resourcePath,  "CSPMGraphSynthesis", errorPath).toString();

        List<String> revealedProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace");
        revealedProcessesTraceString = "<Trace, Value, Key, Trace, Trace>";
        List<String> revealedTickProcessesTrace = List.of("Trace", "Value", "Key", "Trace", "Trace","✓");
        revealedTickProcessesTraceString = "<Trace, Value, Key, Trace, Trace>";
        List<String> processesTrace = List.of("τ", "Value", "Key", "τ", "τ");
        processesTraceString = "<τ, Value, Key, τ, τ>";
        List<String> processesTickTrace = List.of("τ", "Value", "Key", "τ", "τ","✓");
        processesTickTraceString = "<τ, Value, Key, τ, τ>";
        List<String> noTauTrace = List.of("Value", "Key","✓");
        noTauTraceString = "<Value, Key>";
        List<String> noTauTickTrace = List.of("Value", "Key");
        noTauTickTraceString = "<Value, Key>";
        Set<String> hidden = Set.of("Trace");
        hiddenString = "{Trace}";

        List<String> revealedTauProcessesTrace = List.of("τ", "Value", "Key", "Trace", "Trace");
        revealedTauProcessesTraceString = "<τ, Value, Key, Trace, Trace>";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("error", "trace");

        ObjectNode recursionNode = mapper.createObjectNode();
        String recursionError = "An operator that cannot be recursed through was recursed through in the case of test = path -> path2 -> test";
        recursionNode.put("error", recursionError);
        ObjectNode largeNode = mapper.createObjectNode();
        largeError = "An extremely long error message exceeding the 64 character used to indicator a possible recursion issue";
        largeNode.put("error", largeError);

        FDRCounterexample fdrCounterexample = spy(FDRCounterexample.class);
        fdrCounterexample.setRevealedProcessesTrace(revealedProcessesTrace);
        fdrCounterexample.setProcessesTrace(processesTrace);
        when(fdrCounterexample.getNoTauTrace()).thenReturn(noTauTrace);
        when(fdrCounterexample.getHidden()).thenReturn(hidden);

        FDRCounterexample fdrTickCounterexample = spy(FDRCounterexample.class);
        fdrTickCounterexample.setRevealedProcessesTrace(revealedTickProcessesTrace);
        fdrTickCounterexample.setProcessesTrace(processesTickTrace);
        when(fdrTickCounterexample.getNoTauTrace()).thenReturn(noTauTickTrace);
        when(fdrTickCounterexample.getHidden()).thenReturn(hidden);

        FDRCounterexample fdrTauCounterexample = spy(FDRCounterexample.class);
        fdrTauCounterexample.setRevealedProcessesTrace(revealedTauProcessesTrace);
        fdrTauCounterexample.setProcessesTrace(processesTrace);
        when(fdrTauCounterexample.getNoTauTrace()).thenReturn(noTauTrace);
        when(fdrTauCounterexample.getHidden()).thenReturn(hidden);

        FDRCounterexample fdrEmptyCounterexample = spy(FDRCounterexample.class);
        fdrEmptyCounterexample.setRevealedProcessesTrace(List.of());
        fdrEmptyCounterexample.setProcessesTrace(null);
        fdrEmptyCounterexample.setProcessesTrace(List.of());
        when(fdrEmptyCounterexample.getNoTauTrace()).thenReturn(List.of());
        when(fdrEmptyCounterexample.getHidden()).thenReturn(Set.of());

        FDRResults fdrFailedResults = spy(FDRResults.class);
        when(fdrFailedResults.getFdrCounterexamples()).thenReturn(List.of(fdrCounterexample, fdrEmptyCounterexample));
        when(fdrFailedResults.isPassed()).thenReturn(false);
        when(fdrFailedResults.getAssertionString()).thenReturn(type);

        FDRResults fdrTickResults = spy(FDRResults.class);
        when(fdrTickResults.getFdrCounterexamples()).thenReturn(List.of(fdrTickCounterexample, fdrEmptyCounterexample));
        when(fdrTickResults.isPassed()).thenReturn(false);
        when(fdrTickResults.getAssertionString()).thenReturn(type);

        FDRResults fdrErrorResults = spy(FDRResults.class);
        when(fdrErrorResults.isPassed()).thenReturn(false);
        when(fdrErrorResults.getAssertionString()).thenReturn(type);
        when(fdrErrorResults.getErrors()).thenReturn(List.of(node));

        FDRResults fdrResults = spy(FDRResults.class);
        when(fdrResults.isPassed()).thenReturn(true);
        when(fdrResults.getAssertionString()).thenReturn(type);

        FDRResults fdrTauResults = spy(FDRResults.class);
        when(fdrTauResults.getFdrCounterexamples()).thenReturn(List.of(fdrTauCounterexample));
        when(fdrTauResults.isPassed()).thenReturn(false);
        when(fdrTauResults.getAssertionString()).thenReturn(type);

        fdrOutput = new FDROutput();
        fdrOutput.addFdrResults(fdrResults);
        fdrTauOutput = new FDROutput();
        fdrTauOutput.addFdrResults(fdrTauResults);
        fdrVerErrorOutput = new FDROutput();
        fdrVerErrorOutput.addFdrResults(fdrErrorResults);
        fdrFailureOutput = new FDROutput();
        fdrFailureOutput.addFdrResults(fdrFailedResults);
        fdrTickOutput = new FDROutput();
        fdrTickOutput.addFdrResults(fdrTickResults);

        fdrErrorOutput = new FDROutput();
        fdrErrorOutput.addError(mapper.valueToTree(node.get("error")));
        fdrRecursionErrorOutput = new FDROutput();
        fdrRecursionErrorOutput.addError(mapper.valueToTree(recursionNode.get("error")));
        fdrLargeErrorOutput = new FDROutput();
        fdrLargeErrorOutput.addError(mapper.valueToTree(largeNode.get("error")));

        headerText = "\"CSP\",\"Assertion\",\"Rerun Assertion\",\"Passed\",\"CounterExample\"," +
                "\"Revealed_Trace\",\"No_Taus\",\"Hidden\"";
        errorHeaderText = "\"CSP\",\"Assertion\",\"ErrorType\"";

    }

    @AfterEach
    public void CleanUp() throws IOException{
        File dataFile = new File(filePath);
        dataFile.delete();
        File errorFile = new File(errorFilePath);
        errorFile.delete();
    }

    @Test
    public void givenValidResourcePath_whenInitialiseDatasetGenerator_thenCreateFile() throws IOException {
        @SuppressWarnings("unchecked")

        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        assertEquals(filePath, datasetGenerator.getDatasetPath());
        File dataFile = new File(datasetGenerator.getDatasetPath());
        assertTrue(dataFile.exists(), "The data file does not exist");

        assertEquals(errorFilePath, datasetGenerator.getErrorPath());
        dataFile = new File(datasetGenerator.getErrorPath());
        assertTrue(dataFile.exists(), "The error file does not exist");
    }

    @Test
    public void givenExistingFile_whenInitialiseDatasetGenerator_thenFileExistsAndMessagePrinted() throws IOException {
        @SuppressWarnings("unchecked")
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        File dataFile = new File(filePath);
        dataFile.createNewFile();
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        assertEquals(filePath, datasetGenerator.getDatasetPath());
        assertTrue(dataFile.exists(), "The file does not exist");

        assertTrue(
                outContent.toString()
                        .endsWith("File already existed at: "+filePath
                                +System.getProperty("line.separator")+
                                "File created at: "+errorFilePath
                                +System.getProperty("line.separator")),
                "Expected the stream to end with given message but was "+outContent.toString());
    }

    @Test
    public void givenExistingErrorFile_whenInitialiseDatasetGenerator_thenFileExistsAndMessagePrinted() throws IOException {
        @SuppressWarnings("unchecked")
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        File dataFile = new File(errorFilePath);
        dataFile.createNewFile();

        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        assertEquals(errorFilePath, datasetGenerator.getErrorPath());
        assertTrue(dataFile.exists(), "The error file does not exist");

        assertTrue(
                outContent.toString()
                        .endsWith("File already existed at: "+errorFilePath
                                +System.getProperty("line.separator")),
                "Expected the stream to end with given message but was "+outContent.toString());
    }

    @Test
    public void givenValidCSPAndPassingOutput_whenAddEntryToDataset_thenDataFilePopulated() throws IOException {
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "passingCSP";
        StringBuilder entryText = new StringBuilder();
        entryText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"").append(",")
                .append("\"").append("\"").append(",")
                .append("\"").append(true).append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("{}").append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(entryText.toString(), entry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        assertEquals(0, errorFile.length(), "Error file has content");
    }

    @Test
    public void givenValidCSPAndFailingOutput_whenAddEntryToDataset_thenDataFilePopulated() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "failingCSP";
        StringBuilder firstEntryText = new StringBuilder();
        firstEntryText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"").append(",")
                .append("\"").append("\"").append(",")
                .append("\"").append(false).append("\"").append(",")
                .append("\"").append(processesTraceString).append("\"").append(",")
                .append("\"").append(revealedProcessesTraceString).append("\"").append(",")
                .append("\"").append(noTauTraceString).append("\"").append(",")
                .append("\"").append(hiddenString).append("\"");
        StringBuilder secondEntryText = new StringBuilder();
        secondEntryText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"").append(",")
                .append("\"").append("\"").append(",")
                .append("\"").append(false).append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("{}").append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrFailureOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(firstEntryText.toString(), entry, "Entry is unexpected: "+entry);
            String secondEntry = br.readLine();
            assertEquals(secondEntryText.toString(), secondEntry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        assertEquals(0, errorFile.length(), "Error file has content");
    }

    @Test
    public void givenValidCSPAndFailingOutputWithTicks_whenAddEntryToDataset_thenDataFilePopulated() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "failingCSP";
        StringBuilder firstEntryText = new StringBuilder();
        firstEntryText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"").append(",")
                .append("\"").append("\"").append(",")
                .append("\"").append(false).append("\"").append(",")
                .append("\"").append(processesTickTraceString).append("\"").append(",")
                .append("\"").append(revealedTickProcessesTraceString).append("\"").append(",")
                .append("\"").append(noTauTickTraceString).append("\"").append(",")
                .append("\"").append(hiddenString).append("\"");
        StringBuilder secondEntryText = new StringBuilder();
        secondEntryText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"").append(",")
                .append("\"").append("\"").append(",")
                .append("\"").append(false).append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("<>").append("\"").append(",")
                .append("\"").append("{}").append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrTickOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(firstEntryText.toString(), entry, "Entry is unexpected: "+entry);
            String secondEntry = br.readLine();
            assertEquals(secondEntryText.toString(), secondEntry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        assertEquals(0, errorFile.length(), "Error file has content");
    }

    @Test
    public void givenValidCSPAndFailingOutputWithTau_whenAddEntryToDataset_thenDataFilePopulatedWithoutTauInHidden() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "failingCSP";
        StringBuilder firstEntryText = new StringBuilder();
        firstEntryText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"").append(",")
                .append("\"").append("\"").append(",")
                .append("\"").append(false).append("\"").append(",")
                .append("\"").append(processesTraceString).append("\"").append(",")
                .append("\"").append(revealedTauProcessesTraceString).append("\"").append(",")
                .append("\"").append(noTauTraceString).append("\"").append(",")
                .append("\"").append(hiddenString).append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrTauOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(firstEntryText.toString(), entry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        assertEquals(0, errorFile.length(), "Error file has content");
    }


    @Test
    public void givenInvalidCSPAndErrorOutput_whenAddEntryToDataset_thenErrorFilePopulated() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "error";
        StringBuilder errorText = new StringBuilder();
        errorText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append("\"").append(",").append("\"")
                .append("trace").append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrErrorOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(errorFile))) {
            String header = br.readLine();
            assertEquals(errorHeaderText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(errorText.toString(), entry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "Error still has content: "+nextLine);
        }

    }

    @Test
    public void givenInvalidCSPAndLargeErrorOutput_whenAddEntryToDataset_thenErrorFilePopulated() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "error";
        StringBuilder errorText = new StringBuilder();
        errorText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append("\"").append(",").append("\"")
                .append(largeError).append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrLargeErrorOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(errorFile))) {
            String header = br.readLine();
            assertEquals(errorHeaderText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(errorText.toString(), entry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "Error still has content: "+nextLine);
        }

    }

    @Test
    public void givenInvalidCSPAndRecErrorOutput_whenAddEntryToDataset_thenErrorFilePopulated() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "error";
        StringBuilder errorText = new StringBuilder();
        errorText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append("\"").append(",").append("\"")
                .append("Operator cannot be recursed through").append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrRecursionErrorOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(errorFile))) {
            String header = br.readLine();
            assertEquals(errorHeaderText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(errorText.toString(), entry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "Error still has content: "+nextLine);
        }

    }

    @Test
    public void givenValidCSPAndFDRErrorOutput_whenAddEntryToDataset_thenErrorFilePopulated() throws IOException{
        @SuppressWarnings("unchecked")
        DatasetGenerator datasetGenerator = new DatasetGenerator(resourcePath, datasetPath);

        String csp = "errorCSP";
        StringBuilder errorText = new StringBuilder();
        errorText.append("\"").append(csp).append("\"").append(",")
                .append("\"").append(type).append("\"")
                .append(",").append("\"").append("\"");

        datasetGenerator.addEntryToDataSet(csp, fdrVerErrorOutput);

        File dataFile = new File(filePath);
        File errorFile = new File(errorFilePath);

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String header = br.readLine();
            assertEquals(headerText, header, "Header is unexpected: "+header);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(errorFile))) {
            String header = br.readLine();
            assertEquals(errorHeaderText, header, "Header is unexpected: "+header);
            String entry = br.readLine();
            assertEquals(errorText.toString(), entry, "Entry is unexpected: "+entry);
            String nextLine = br.readLine();
            assertNull(nextLine, "Error still has content: "+nextLine);
        }

    }

}
