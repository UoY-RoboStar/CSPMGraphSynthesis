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

    /*public void addEntryToDataSet(String csp, FDROutput dataEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.datasetPath, true))){
            //FileWriter wr = new FileWriter(this.datasetPath);

            

            for (FDRResults fdrResults: dataEntry.getFdrResults()){
                boolean passed = fdrResults.isPassed();
                if (passed){
                    writer.write(csp);
                    writer.write(", true, " );
                    writer.newLine();
                } else {
                    for (FDRCounterexample counterexample : fdrResults.getFdrCounterexamples()){
                        writer.write(csp);
                        writer.write(", false, ");
                        writer.write(counterexample.getProcessesTrace().toString());
                        writer.newLine();
                    }
                }

            }
        } catch ( IOException ex ) {
            System.out.println(ex);
        }
    }*/

    /*public void addEntryToDataSet(String csp, FDROutput dataEntry){
        try (
            OutputStream fileOut = Files.newOutputStream(Paths.get(this.datasetPath));
            Workbook wb = new Workbook(fileOut,"MyApplication", "1.0")){
            Worksheet ws = wb.newWorksheet("Deadlock Freedom");
            ws.width(0, 25);
            ws.width(1, 15);

            ws.range(0, 0, 0, 1).style().fontName("Arial").fontSize(16).bold().fillColor("3366FF").set();
            ws.value(0, 0, "CSP");
            ws.value(0, 1, "Passed");
            ws.value(0,2,"CounterExample");

            int row = 2;

            for (FDRResults fdrResults: dataEntry.getFdrResults()){
                boolean passed = fdrResults.isPassed();
                if (passed){
                    ws.value(row, 0, csp);
                    ws.value(row, 1, true);
                    ws.value(row, 1, "");
                    row++;
                    System.out.println("Row added for passed test, to file: " + fileOut);

                } else {
                    for (FDRCounterexample counterexample : fdrResults.getFdrCounterexamples()){
                        ws.range(row, 0, 2, 1).style().wrapText(true).set();
                        ws.value(row, 0, csp);
                        ws.value(row, 1, false);
                        ws.value(row, 1, counterexample.getProcessesTrace().toString());
                        row++;
                        System.out.println("Row added for failed test, to file: " + fileOut);
                    }
                }
            }

        } catch ( Exception ex ) {
            System.out.println(ex);
        }
    }*/

    private void emptyDataset(){
        this.exists = new File(this.datasetPath).length() != 0;
    }

    private void emptyErrorset(){
        this.errorExists = new File(this.errorPath).length() != 0;
    }

    public void addEncodedCspEntryToDataSet(String csp, FDROutput dataEntry){
        //String encodedString = Base64.getEncoder().encodeToString(csp.getBytes());
        //String encodedString = csp.replace("\n", "<NL>");
        addEntryToDataSet(csp, dataEntry);
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
            emptyErrorset();
            FileWriter fileWriter = new FileWriter(this.errorPath, this.errorExists);
            CSVWriter writer = new CSVWriter(fileWriter);

            if (!this.errorExists) {
                String[] headings = new String[]{"CSP", "Assertion", "ErrorType"};
                writer.writeNext(headings);
                this.errorFileSize += 3;
            }

            for (JsonNode error : errors) {
                if (error.size()>64){
                    if (error.asText().substring(0,64).matches("^An operator that cannot be recursed through was recursed through$")) {
                        String[] entry = new String[]{csp, assertion, "Operator cannot be recursed through"};
                        writer.writeNext(entry);
                        this.errorFileSize += 3;
                        System.out.println("Row added for error, to file: " + this.errorPath);
                        continue;
                    }
                }
                else {
                    String[] entry = new String[]{csp, assertion, error.asText()};
                    writer.writeNext(entry);
                    this.errorFileSize += 3;
                    System.out.println("Row added for error, to file: " + this.errorPath);
                }
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
