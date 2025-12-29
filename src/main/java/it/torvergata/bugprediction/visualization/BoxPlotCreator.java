package it.torvergata.bugprediction.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoxPlotCreator {

    public static void createBoxplot(List<Experiment> experiments) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        // Raggruppiamo per label unica
        experiments.stream()
                .map(Experiment::getLabel)
                .distinct()
                .forEach(label -> {
                    List<Double> precisionValues = new ArrayList<>();
                    List<Double> recallValues = new ArrayList<>();
                    for (Experiment e : experiments) {
                        if (e.getLabel().equals(label)) {
                            precisionValues.add(e.getPrecision());
                            recallValues.add(e.getRecall());
                        }
                    }
                    dataset.add(precisionValues, "Precision", label);
                    dataset.add(recallValues, "Recall", label);
                });

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "Precision vs Recall",
                "Label",
                "Value",
                dataset,
                true);

        // Mostra in JFrame
        JFrame frame = new JFrame("Boxplot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static void createBoxplotIgnoringBalancing(List<Experiment> experiments) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        // Raggruppiamo per label senza balancing
        experiments.stream()
                .map(Experiment::getLabelWithoutBalancing)
                .distinct()
                .forEach(label -> {
                    List<Double> precisionValues = new ArrayList<>();
                    List<Double> recallValues = new ArrayList<>();
                    for (Experiment e : experiments) {
                        if (e.getLabelWithoutBalancing().equals(label)) {
                            precisionValues.add(e.getPrecision());
                            recallValues.add(e.getRecall());
                        }
                    }
                    dataset.add(precisionValues, "Precision", label);
                    dataset.add(recallValues, "Recall", label);
                });

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "Precision vs Recall (ignoring Balancing)",
                "Label",
                "Value",
                dataset,
                true);

        // Mostra in JFrame
        JFrame frame = new JFrame("Boxplot senza Balancing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static void createMultipleBoxplotsByBalancing(List<Experiment> experiments, String project) {

        // Prendiamo tutti i balancing distinti
        Set<String> balancingValues = experiments.stream()
                .map(Experiment::getBalancing)
                .collect(Collectors.toSet());

        // Per ogni balancing creiamo UN grafico
        for (String balancing : balancingValues) {

            // filtriamo gli esperimenti di quel balancing
            List<Experiment> filtered = experiments.stream()
                    .filter(e -> e.getBalancing().equals(balancing))
                    .toList();

            DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

            // Raggruppiamo senza il balancing
            filtered.stream()
                    .map(Experiment::getLabelWithoutBalancing)
                    .distinct()
                    .forEach(label -> {
                        List<Double> precisionValues = new ArrayList<>();
                        List<Double> recallValues = new ArrayList<>();
                        for (Experiment e : filtered) {
                            if (e.getLabelWithoutBalancing().equals(label)) {
                                precisionValues.add(e.getPrecision());
                                recallValues.add(e.getRecall());
                            }
                        }
                        dataset.add(precisionValues, "Precision", label);
                        dataset.add(recallValues, "Recall", label);
                    });

            // Creiamo il grafico
            JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                    project + "\nPrecision vs Recall — Balancing: " + balancing,
                    "Classifier + Feature Selection + Cost",
                    "Value",
                    dataset,
                    true
            );

            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            CategoryAxis axis = plot.getDomainAxis();
            axis.setCategoryLabelPositions(
                    CategoryLabelPositions.UP_90
            );

            // Mostra finestra
            JFrame frame = new JFrame(project + " — Balancing = " + balancing);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ChartPanel(chart));
            frame.pack();
            frame.setVisible(true);
        }
    }

    public static void createGridBoxplotsByBalancing(List<Experiment> experiments) {

        Set<String> balancingValues = experiments.stream()
                .map(Experiment::getBalancing)
                .collect(Collectors.toSet());

        for (String balancing : balancingValues) {

            List<Experiment> filtered = experiments.stream()
                    .filter(e -> e.getBalancing().equals(balancing))
                    .toList();

            Set<String> classifiers = filtered.stream().map(Experiment::getClassifier).collect(Collectors.toSet());
            Set<String> featureSelections = filtered.stream().map(Experiment::getFeatureSelection).collect(Collectors.toSet());

            JPanel panel = new JPanel(new GridLayout(classifiers.size(), featureSelections.size()));

            for (String classifier : classifiers) {
                for (String feature : featureSelections) {

                    List<Experiment> cellData = filtered.stream()
                            .filter(e -> e.getClassifier().equals(classifier) && e.getFeatureSelection().equals(feature))
                            .collect(Collectors.toList());

                    DefaultBoxAndWhiskerCategoryDataset ds = new DefaultBoxAndWhiskerCategoryDataset();
                    List<Double> precisions = cellData.stream().map(Experiment::getPrecision).collect(Collectors.toList());
                    List<Double> recalls = cellData.stream().map(Experiment::getRecall).collect(Collectors.toList());

                    ds.add(precisions, "Precision", "");
                    ds.add(recalls, "Recall", "");

                    JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                            classifier + " - " + feature,
                            "",
                            "",
                            ds,
                            false
                    );

                    CategoryPlot plot = (CategoryPlot) chart.getPlot();
                    plot.getDomainAxis().setVisible(false);  // nasconde asse X
                    plot.getRangeAxis().setVisible(false);   // nasconde asse Y

                    panel.add(new ChartPanel(chart));
                }
            }

            JFrame frame = new JFrame("Boxplot Grid — Balancing: " + balancing);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        }
    }
}