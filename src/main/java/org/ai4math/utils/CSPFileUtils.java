package org.ai4math.utils;

import com.opencsv.CSVReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSPFileUtils {

    private final String cspFilePath = "CSPMGraphSynthesis";

    public File getDirectory(String path) throws IOException{
        String directoryPath = path==null?System.getProperty("user.home"):path;
        Path dir = Paths.get(directoryPath, this.cspFilePath);

        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
            return dir.toFile();
        } catch (IOException e) {
            System.out.println("Failed to create directory at: " + dir.toString());
            throw e;
        }
    }

    public String getResourcePath(String resourcePath, String resourceName) throws IOException{
        //ClassLoader classLoader = getClass().getClassLoader();
        //File file = new File(classLoader.getResource(resourceName).getFile());
        File file = new File(getDirectory(resourcePath), resourceName);

        return file.getAbsolutePath();
    }

    public String createCSPFile(String path, String resourceName, String csp) throws IOException{
        File cspFile = new File(getDirectory(path), resourceName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cspFile));
            writer.write(csp);
            writer.close();
        } catch (IOException ex){
            System.out.println("Encountered error while creating CSP file at location: "
                    + cspFile.getAbsolutePath() + " with error: "+ ex);
        }

        return cspFile.getAbsolutePath();
    }

    public List<String> getCSPFiles(String resourcePath) throws IOException{
        List<String> files = List.of();
        String path = getDirectory(resourcePath).getAbsolutePath();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Exception encountered when retrieving csp files: " + e.getMessage());
            return files;
        }
        return files;
    }

    public long getFileSize(File file){
        return file.length() / ((1024 * 1024));
    }

    public void createNewDataFile(File file) throws IOException{
        File oldFile = file;
        String name = file.getPath().replace(".csv", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date())) + ".csv";
        oldFile.renameTo(new File(name));
        file.createNewFile();
    }
}
