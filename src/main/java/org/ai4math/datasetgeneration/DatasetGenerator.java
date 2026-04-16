package org.ai4math.datasetgeneration;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.CSVWriter;
import org.ai4math.utils.CSPFileUtils;
import org.ai4math.vandv.utils.FDRCounterexample;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;

import java.io.*;
import java.util.List;

public class DatasetGenerator {
    private String datasetPath;
    private String errorPath;
    private boolean exists;
    private long fileSize;
    private long errorFileSize;
    private boolean errorExists;

    public DatasetGenerator(String resourcePath, String datasetPath) throws IOException{
        this.datasetPath = new CSPFileUtils().getResourcePath(resourcePath, datasetPath);
        this.errorPath = new CSPFileUtils().getResourcePath(resourcePath, "Error.csv");

        try{
            File dataFile = new File(this.datasetPath);
            if (dataFile.createNewFile()){
                System.out.println("File created at: "+this.datasetPath);
            } else {
                System.out.println("File already existed at: "+this.datasetPath);
                checkDataFileSize(dataFile);
            }
        } catch (IOException e){
            System.out.println("An error occurred when creating the dataset file at: "+this.datasetPath);
        }

        try{
            File errorFile = new File(this.errorPath);
            if (errorFile.createNewFile()){
                System.out.println("File created at: "+this.errorPath);
            } else {
                System.out.println("File already existed at: "+this.errorPath);
            }
        } catch (IOException e){
            System.out.println("An error occurred when creating the error file at: "+this.errorPath);
        }

    }

    public String getDatasetPath() {
        return datasetPath;
    }
    public String getErrorPath() { return errorPath; }

    private void emptyDataset(){
        this.exists = new File(this.datasetPath).length() != 0;
    }

    private void emptyErrorset(){
        this.errorExists = new File(this.errorPath).length() != 0;
    }

    public void addEntryToDataSet(String csp, FDROutput dataEntry) {
        try {
            checkDataFileSize(new File(this.datasetPath));
            emptyDataset();
            FileWriter fileWriter = new FileWriter(this.datasetPath, this.exists);
            CSVWriter writer = new CSVWriter(fileWriter);

            if (!this.exists) {
                String[] headings = new String[]{"CSP", "Assertion", "Passed", "CounterExample", "Revealed_Trace", "No_Taus", "Hidden"};
                writer.writeNext(headings);
                this.fileSize += 7;
            }
            if (dataEntry.getErrors() != null) {
                addErrorToDataset(csp, "", dataEntry.getErrors());
            }
            else {
                for (FDRResults fdrResults : dataEntry.getFdrResults()) {

                    boolean passed = fdrResults.isPassed();
                    String assertion = fdrResults.getAssertionString();
                    if (passed) {
                        String[] entry = new String[]{
                                csp,
                                assertion,
                                "true",
                                "<>",
                                "<>",
                                "<>",
                                "{}"
                        };
                        writer.writeNext(entry);
                        this.fileSize += 7;
                        System.out.println("Row added for passed test, to file: " + this.datasetPath);
                    } else {
                        if (fdrResults.getFdrCounterexamples() == null) {
                            addErrorToDataset(
                                    csp,
                                    assertion,
                                    fdrResults.getErrors()
                            );
                        } else {
                            for (FDRCounterexample counterexample : fdrResults.getFdrCounterexamples()) {
                                String[] entry;
                                if (counterexample.getProcessesTrace() == null) {
                                    entry = new String[]{
                                            csp,
                                            assertion,
                                            "false",
                                            "<>",
                                            "<>",
                                            "<>",
                                            "{}"
                                    };
                                    this.fileSize += 7;
                                } else {
                                    entry = new String[]{
                                            csp,
                                            assertion,
                                            "false",
                                            counterexample.getProcessesTrace().toString().replace('[','<').replace(']', '>'),
                                            counterexample.getRevealedProcessesTrace().toString().replace('[','<').replace(']', '>'),
                                            counterexample.getNoTauTrace().toString().replace('[','<').replace(']', '>'),
                                            counterexample.getHidden().toString().replace('[', '{').replace(']', '}')
                                    };
                                    this.fileSize += 7;
                                }
                                writer.writeNext(entry);
                                System.out.println("Row added for failed test, to file: " + this.datasetPath);
                            }
                        }
                    }
                }
            }

            fileWriter.flush();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void addErrorToDataset(String csp, String assertion, List<JsonNode> errors) {
        try {
            checkErrorFileSize(new File(this.errorPath));
            emptyErrorset();
            FileWriter fileWriter = new FileWriter(this.errorPath, this.errorExists);
            CSVWriter writer = new CSVWriter(fileWriter);

            if (!this.errorExists) {
                String[] headings = new String[]{"CSP", "Assertion", "ErrorType"};
                writer.writeNext(headings);
                this.errorFileSize += 3;
            }

            for (JsonNode error : errors) {
                int size = error.isEmpty()?error.asText().length():error.size();
                if (size>64){
                    if (error.asText().substring(0,64).matches("^An operator that cannot be recursed through was recursed through$")) {
                        String[] entry = new String[]{csp, assertion, "Operator cannot be recursed through"};
                        writer.writeNext(entry);
                        this.errorFileSize += 3;
                        System.out.println("Row added for error, to file: " + this.errorPath);
                        continue;
                    }
                }
                String[] entry = new String[]{csp, assertion, error.asText()};
                writer.writeNext(entry);
                this.errorFileSize += 3;
                System.out.println("Row added for error, to file: " + this.errorPath);
            }

            fileWriter.flush();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void checkDataFileSize(File dataFile) {
        try {
            this.fileSize = new CSPFileUtils().getFileSize(dataFile);

            if (this.fileSize > 95){
                new CSPFileUtils().createNewDataFile(dataFile);
                this.fileSize = 0;
            }
        } catch (IOException e){
            System.out.println("An error occurred when creating a new data file at: "+this.datasetPath);
        }
    }

    public void checkErrorFileSize(File dataFile) {
        try {
            this.errorFileSize = new CSPFileUtils().getFileSize(dataFile);

            if (this.errorFileSize > 95){
                new CSPFileUtils().createNewDataFile(dataFile);
                this.errorFileSize = 0;
            }
        } catch (IOException e){
            System.out.println("An error occurred when creating a new error file at: "+this.errorPath);
        }
    }

}
