package org.ai4math.utils;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CSPFileUtilsTest {

    @Test
    public void givenValidFilePath_whenCreateNewDataFile_thenFileGenerated() throws IOException {
        @SuppressWarnings("unchecked")
        CSPFileUtils fileUtils = new CSPFileUtils();
        String resourcePath = System.getProperty("user.home");
        String fileName = "cspfilesTest.csv";
        Path dir = Paths.get(resourcePath, "test", "fileutils", "CSPMGraphSynthesis");
        resourcePath = dir.toAbsolutePath().toString();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        File filePath = new File(resourcePath, fileName);
        filePath.createNewFile();

        String fileContent = "The test of createNewDataFile";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(fileContent);
        }

        assertTrue(filePath.length()>0, "File at "+filePath+" is empty");

        fileUtils.createNewDataFile(filePath);

        String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
        String datedPath = filePath.getPath().replace(".csv", date+".csv");
        File datedFile = new File(datedPath);

        assertTrue(filePath.exists(), "File doesn't exist");
        assertEquals(0, filePath.length(), "File is nonempty");
        assertTrue(datedFile.exists(), "Dated file not created");
        assertNotEquals(0, datedFile.length(), "Dated file is empty");

        try (BufferedReader br = new BufferedReader(new FileReader(datedFile))) {
            String content = br.readLine();
            assertEquals(fileContent, content, "File contents is unexpected: "+fileContent);
            String nextLine = br.readLine();
            assertNull(nextLine, "File still has content: "+nextLine);
        }

        datedFile.delete();
        filePath.delete();
    }

    @Test
    public void givenValidCSPFilePath_whenGetCSPFiles_thenListReturned() throws IOException {
        @SuppressWarnings("unchecked")
        CSPFileUtils fileUtils = new CSPFileUtils();
        String resourcePath = System.getProperty("user.home");
        String file1Name = "cspfiles1Test.csp";
        String file2Name = "cspfiles2Test.csp";
        String file3Name = "cspfiles3Test.csp";
        String file4Name = "cspfiles4Test.csv";
        String resourceFolderPath = Paths.get(resourcePath, "test", "fileutils").toString();
        Path dir = Paths.get(resourceFolderPath, "CSPMGraphSynthesis");
        resourcePath = dir.toAbsolutePath().toString();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        File file1Path = new File(resourcePath, file1Name);
        file1Path.createNewFile();
        File file2Path = new File(resourcePath, file2Name);
        file2Path.createNewFile();
        File file3Path = new File(resourcePath, file3Name);
        file3Path.createNewFile();
        File file4Path = new File(resourcePath, file4Name);
        file4Path.createNewFile();

        List<String> files = fileUtils.getCSPFiles(resourceFolderPath);

        assertEquals(3, files.size(), "File list doesn't have three csp files");
        assertTrue(files.contains(file1Path.toString()), "File1 not included in files: "+files);
        assertTrue(files.contains(file2Path.toString()), "File2 not included in files: "+files);
        assertTrue(files.contains(file3Path.toString()), "File3 not included in files: "+files);

        file1Path.delete();
        file2Path.delete();
        file3Path.delete();
        file4Path.delete();
    }

    @Test
    public void givenInvalidCSPFilePath_whenGetCSPFiles_thenEmptyListReturned() throws IOException {
        @SuppressWarnings("unchecked")
        CSPFileUtils fileUtils = new CSPFileUtils();
        String resourcePath = System.getProperty("user.home");
        String file1Name = "cspfiles1Test.txt";
        String file2Name = "cspfiles2Test.pdf";
        String file3Name = "cspfiles3Test.txt";
        String file4Name = "cspfiles4Test.csv";
        String resourceFolderPath = Paths.get(resourcePath, "test", "fileutils").toString();
        Path dir = Paths.get(resourceFolderPath, "CSPMGraphSynthesis");
        resourcePath = dir.toAbsolutePath().toString();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        File file1Path = new File(resourcePath, file1Name);
        file1Path.createNewFile();
        File file2Path = new File(resourcePath, file2Name);
        file2Path.createNewFile();
        File file3Path = new File(resourcePath, file3Name);
        file3Path.createNewFile();
        File file4Path = new File(resourcePath, file4Name);
        file4Path.createNewFile();

        List<String> files = fileUtils.getCSPFiles(resourceFolderPath);

        assertEquals(0, files.size(), "File list contains files");

        file1Path.delete();
        file2Path.delete();
        file3Path.delete();
        file4Path.delete();
    }

    @Test
    public void givenValidPathNameAndContent_whenCreateCSPFiles_thenFileCreated() throws IOException {
        @SuppressWarnings("unchecked")
        CSPFileUtils fileUtils = new CSPFileUtils();
        String resourcePath = System.getProperty("user.home");
        String file1Name = "cspfiles1Test.csp";
        String content = "CSP Test File Content";
        String resourceFolderPath = Paths.get(resourcePath, "test", "fileutils").toString();
        Path dir = Paths.get(resourceFolderPath, "CSPMGraphSynthesis");

        String filePath = fileUtils.createCSPFile(resourceFolderPath, file1Name, content);

        File file = new File(filePath);
        assertTrue(file.exists(), "File was not created");
        assertEquals(Paths.get(dir.toAbsolutePath().toString(), file1Name).toString(), filePath,
                "File path unexpected: "+filePath);

        file.delete();
    }

    @Test
    public void givenExistingPathName_whenCreateCSPFiles_thenFileCreated() throws IOException {
        @SuppressWarnings("unchecked")
        CSPFileUtils fileUtils = new CSPFileUtils();
        String resourcePath = System.getProperty("user.home");
        String file1Name = "cspfiles1Test.csp";
        String fileContent = "CSP Initial Test File Content";
        String content = "CSP Test File Content";
        String resourceFolderPath = Paths.get(resourcePath, "test", "fileutils").toString();
        Path dir = Paths.get(resourceFolderPath, "CSPMGraphSynthesis");
        String filePath = Paths.get(dir.toAbsolutePath().toString(), file1Name).toString();
        File file = new File(filePath);
        file.createNewFile();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(fileContent);
        }

        long expectedFileSize = file.length();

        String actualFilePath = fileUtils.createCSPFile(resourceFolderPath, file1Name, content);
        File createdFile = new File(actualFilePath);

        assertTrue(createdFile.exists(), "File exists");
        assertEquals(filePath, actualFilePath,
                "File path unexpected: "+actualFilePath);
        assertNotEquals(expectedFileSize, createdFile.length(), "The file sizes match");

        file.delete();
        createdFile.delete();
    }
}
