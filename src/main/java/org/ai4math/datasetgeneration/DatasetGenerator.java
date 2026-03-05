package org.ai4math.datasetgeneration;

import com.opencsv.CSVWriter;
import org.ai4math.utils.CSPFileUtils;
import org.ai4math.vandv.utils.FDRCounterexample;
import org.ai4math.vandv.utils.FDROutput;
import org.ai4math.vandv.utils.FDRResults;

import java.io.*;
import java.util.Base64;

public class DatasetGenerator {
    private String datasetPath;
    private boolean exists;

    public DatasetGenerator(String datasetPath){
        this.datasetPath = new CSPFileUtils().getResourcePath(datasetPath);

        try{
            File dataFile = new File(this.datasetPath);
            if (dataFile.createNewFile()){
                System.out.println("File created at: "+this.datasetPath);
            } else {
                System.out.println("File already existed at: "+this.datasetPath);
            }
        } catch (IOException e){
            System.out.println("An error occurred when creating the dataset file at: "+this.datasetPath);
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

    public void addEncodedCspEntryToDataSet(String csp, FDROutput dataEntry){
        String encodedString = Base64.getEncoder().encodeToString(csp.getBytes());
        addEntryToDataSet(encodedString, dataEntry);
    }

    public void addEntryToDataSet(String csp, FDROutput dataEntry) {
        try {
            emptyDataset();
            FileWriter fileWriter = new FileWriter(this.datasetPath, this.exists);
            CSVWriter writer = new CSVWriter(fileWriter);

            if (!this.exists) {
                String[] headings = new String[]{"CSP", "Passed", "CounterExample"};
                writer.writeNext(headings);
            }

            for (FDRResults fdrResults : dataEntry.getFdrResults()) {
                boolean passed = fdrResults.isPassed();
                if (passed) {
                    String[] entry = new String[]{csp, "true", ""};
                    writer.writeNext(entry);
                    System.out.println("Row added for passed test, to file: " + this.datasetPath);
                } else {
                    for (FDRCounterexample counterexample : fdrResults.getFdrCounterexamples()) {
                        String[] entry = new String[]{csp, "false", counterexample.getProcessesTrace().toString()};
                        writer.writeNext(entry);
                        System.out.println("Row added for failed test, to file: " + this.datasetPath);
                    }
                }
            }

            fileWriter.flush();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
