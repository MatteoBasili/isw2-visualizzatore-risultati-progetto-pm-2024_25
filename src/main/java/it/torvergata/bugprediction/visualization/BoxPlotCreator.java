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

    public static void createMultipleBoxplotsByBalancing(List<Experiment> experiments, String project) {

        Set<String> balancingValues = experiments.stream()
                .map(Experiment::getBalancing)
                .collect(Collectors.toSet());

        for (String balancing : balancingValues) {

            List<Experiment> filtered = experiments.stream()
                    .filter(e -> e.getBalancing().equals(balancing))
                    .toList();

            // Dataset per F1, AUC, NpofB25
            DefaultBoxAndWhiskerCategoryDataset dataset1 = new DefaultBoxAndWhiskerCategoryDataset();
            DefaultBoxAndWhiskerCategoryDataset datasetMcc = new DefaultBoxAndWhiskerCategoryDataset();

            filtered.stream()
                    .map(Experiment::getLabelWithoutBalancing)
                    .distinct()
                    .forEach(label -> {
                        List<Double> f1 = new ArrayList<>();
                        List<Double> auc = new ArrayList<>();
                        List<Double> npofB25 = new ArrayList<>();
                        List<Double> mcc = new ArrayList<>();

                        for (Experiment e : filtered) {
                            if (e.getLabelWithoutBalancing().equals(label)) {
                                f1.add(e.getF1());
                                auc.add(e.getAuc());
                                npofB25.add(e.getNpofB25());
                                mcc.add(e.getMcc());
                            }
                        }
                        dataset1.add(f1, "F1", label);
                        dataset1.add(auc, "AUC", label);
                        dataset1.add(npofB25, "NPofB25", label);

                        datasetMcc.add(mcc, "MCC", label);
                    });

            // === Chart 1: F1, AUC, NPofB25 ===
            JFreeChart chart1 = ChartFactory.createBoxAndWhiskerChart(
                    project + " — " + balancing + "\n(F1 / AUC / NPofB25)",
                    "Classifier + FS + CS",
                    "Value",
                    dataset1,
                    true
            );

            fixAxes(chart1, false);
            setWhiteBackground(chart1);

            JFrame frame1 = new JFrame(project + " — " + balancing + " — F1/AUC/NPofB25");
            frame1.add(new ChartPanel(chart1));
            frame1.pack();
            frame1.setVisible(true);

            // === Chart 2: MCC ===
            JFreeChart chart2 = ChartFactory.createBoxAndWhiskerChart(
                    project + " — " + balancing + "\nMCC",
                    "Classifier + FS + CS",
                    "Value",
                    datasetMcc,
                    true
            );

            fixAxes(chart2, true);
            setWhiteBackground(chart2);

            JFrame frame2 = new JFrame(project + " — " + balancing + " — MCC");
            frame2.add(new ChartPanel(chart2));
            frame2.pack();
            frame2.setVisible(true);
        }
    }

    private static void fixAxes(JFreeChart chart, boolean isMCC) {

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis x = plot.getDomainAxis();
        x.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        if (plot.getRenderer() instanceof org.jfree.chart.renderer.category.BoxAndWhiskerRenderer r) {
            r.setMaximumBarWidth(0.02);
        }

        // Limiti asse Y
        if (isMCC) {
            plot.getRangeAxis().setRange(-1.0, 1.0);
        } else {
            plot.getRangeAxis().setRange(0.0, 1.0);
        }
    }

    private static void setWhiteBackground(JFreeChart chart) {

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

    }

}