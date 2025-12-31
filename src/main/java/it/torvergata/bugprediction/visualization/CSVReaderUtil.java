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
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(in);

            for (CSVRecord r : records) {
                String classifier = r.get("Classifier");
                String feat = r.get("Feature_Selection");
                String bal = r.get("Balancing");
                String cost = r.get("Cost_Sensitive");

                double f1 = Double.parseDouble(r.get("F1"));
                double auc = Double.parseDouble(r.get("AUC"));
                double mcc = Double.parseDouble(r.get("MCC"));
                double npofB25 = Double.parseDouble(r.get("Npofb25"));

                experiments.add(new Experiment(classifier, feat, bal, cost, f1, auc, mcc, npofB25));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore lettura CSV: " + filePath, e);
        }
        return experiments;
    }

}
