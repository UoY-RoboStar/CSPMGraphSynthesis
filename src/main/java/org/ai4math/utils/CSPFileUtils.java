package org.ai4math.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSPFileUtils {


    public String getResourcePath(String resourceName){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        return file.getAbsolutePath();
    }

    public String createCSPFile(String resourceName, String csp){
        File cspFile = new File(resourceName);
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

    public List<String> getCSPFiles(){
        List<String> files = List.of();
        String path = getResourcePath("CSPFiles");

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
}
