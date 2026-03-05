package org.ai4math.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
}
