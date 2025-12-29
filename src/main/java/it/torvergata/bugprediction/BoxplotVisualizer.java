package it.torvergata.bugprediction;

import it.torvergata.bugprediction.visualization.BoxPlotCreator;
import it.torvergata.bugprediction.visualization.CSVReaderUtil;
import it.torvergata.bugprediction.visualization.Experiment;

import java.util.Arrays;
import java.util.List;

public class BoxplotVisualizer {
    public static void main(String[] args) {

        List<String> projects = Arrays.asList(
                "BOOKKEEPER",
                "STORM"
        );
        String basePath = "results/";

        for (String project : projects) {
            String filePath = basePath + project + "_output_final.csv";

            List<Experiment> experiments = CSVReaderUtil.readCSV(filePath);
            BoxPlotCreator.createMultipleBoxplotsByBalancing(experiments, project);
        }
    }
}