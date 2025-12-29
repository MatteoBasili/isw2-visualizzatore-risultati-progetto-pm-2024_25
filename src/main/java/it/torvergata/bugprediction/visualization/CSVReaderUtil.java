package it.torvergata.bugprediction.visualization;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVReaderUtil {

    private static final Logger LOGGER = Logger.getLogger(CSVReaderUtil.class.getName());

    public static List<Experiment> readCSV(String filePath) {
        List<Experiment> experiments = new ArrayList<>();
        try (Reader in = new FileReader(filePath)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()                  // Legge l’header dalla prima riga
                    .setSkipHeaderRecord(true)    // Salta la riga dell’header
                    .build()
                    .parse(in);

            for (CSVRecord record : records) {
                String classifier = record.get("Classifier");
                String featureSelection = record.get("Feature_Selection");
                String balancing = record.get("Balancing");
                String costSensitive = record.get("Cost_Sensitive");
                double precision = Double.parseDouble(record.get("Precision_0.5"));
                double recall = Double.parseDouble(record.get("Recall"));

                experiments.add(new Experiment(classifier, featureSelection, balancing,
                        costSensitive, precision, recall));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la lettura del CSV: " + filePath, e);
        }
        return experiments;
    }
}
