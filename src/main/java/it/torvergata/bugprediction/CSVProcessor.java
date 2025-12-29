package it.torvergata.bugprediction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVProcessor {

    public static void main(String[] args) throws IOException {

        List<String> projects = Arrays.asList(
                "BOOKKEEPER_output",
                "STORM_output"
        );
        String inputBasePath = "src/main/resources/";
        String outputBasePath = "results/";

        // crea la cartella se non esiste
        File outputDir = new File(outputBasePath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            System.err.println("⚠ Impossibile creare la cartella: " + outputBasePath);
        }

        for (String project : projects) {
            String inputFile = inputBasePath + project + ".csv";
            String tempFile = outputBasePath + project + "_temp.csv";
            String outputFile = outputBasePath + project + "_final.csv";

            // Rimuove virgole finali e righe vuote
            cleanCsv(inputFile, tempFile);

            // Aggiunge le colonne e salva il CSV finale
            processCsv(tempFile, outputFile);

            // Rimuove il file temporaneo
            File temp = new File(tempFile);
            if (temp.exists() && !temp.delete()) {
                System.err.println("⚠ Impossibile eliminare il file temporaneo: " + tempFile);
            }

            System.out.println("✔ File processato: " + outputFile);
        }
    }

    // ---------- STEP 1: rimuove virgole finali e righe vuote ----------
    private static void cleanCsv(String inputFile, String outputFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile, StandardCharsets.UTF_8));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(",\\s*$", ""); // rimuove virgola finale
                if (!line.trim().isEmpty()) {         // elimina righe vuote
                    bw.write(line);
                    bw.newLine();
                }
            }
        }
    }

    // ---------- STEP 2: aggiunge le colonne e salva finale ----------
    private static void processCsv(String inputFile, String outputFile) throws IOException {
        FileReader reader = new FileReader(inputFile, StandardCharsets.UTF_8);

        CSVParser parser = CSVParser.parse(
                reader,
                CSVFormat.DEFAULT.builder()
                        .setSkipHeaderRecord(true)
                        .setHeader()
                        .build()
        );

        List<CSVRecord> records = parser.getRecords();
        List<String> originalHeader = new ArrayList<>(parser.getHeaderNames());

        // ------ Nuovo header ------
        List<String> newHeader = new ArrayList<>();
        newHeader.add(originalHeader.get(0)); // Filename
        newHeader.add("Classifier");
        newHeader.add("Feature_Selection");
        newHeader.add("Balancing");
        newHeader.add("Cost_Sensitive");
        newHeader.addAll(originalHeader.subList(1, originalHeader.size()));

        // ------ Scrittura file finale ------
        FileWriter writer = new FileWriter(outputFile, StandardCharsets.UTF_8);
        CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.DEFAULT.builder()
                        .setHeader(newHeader.toArray(new String[0]))
                        .build()
        );

        for (CSVRecord row : records) {
            if (row == null || row.size() == 0 || row.get(0).trim().isEmpty()) continue;

            String filename = row.get(0);
            List<String> newRow = new ArrayList<>();
            newRow.add(filename);
            newRow.add(extract(filename, 1));
            newRow.add(extract(filename, 2));
            newRow.add(extract(filename, 3));
            newRow.add(extract(filename, 4));

            // aggiungi tutte le colonne originali rimanenti
            for (int i = 1; i < originalHeader.size(); i++) {
                newRow.add(row.get(i));
            }

            printer.printRecord(newRow);
        }

        printer.flush();
        printer.close();
        parser.close();
        reader.close();
    }

    // ---------- Helper per estrarre token dal filename ----------
    private static String extract(String filename, int index) {
        String[] parts = filename.replace(".csv", "").split("_");
        return parts.length > index ? parts[index] : "";
    }

}
