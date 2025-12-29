package it.torvergata.bugprediction.visualization;

public class Experiment {
    private final String classifier;
    private final String featureSelection;
    private final String balancing;
    private final String costSensitive;
    private final double precision;
    private final double recall;

    public Experiment(String classifier, String featureSelection, String balancing,
                      String costSensitive, double precision, double recall) {
        this.classifier = classifier;
        this.featureSelection = featureSelection;
        this.balancing = balancing;
        this.costSensitive = costSensitive;
        this.precision = precision;
        this.recall = recall;
    }

    // getter
    public String getClassifier() { return classifier; }
    public String getFeatureSelection() { return featureSelection; }
    public String getBalancing() { return balancing; }
    public double getPrecision() { return precision; }
    public double getRecall() { return recall; }

    // per etichetta unica (es. "IBk_NoSelection_NoSampling_yesCostSensitive")
    public String getLabel() {
        return classifier + "_" + featureSelection + "_" + balancing + "_" + costSensitive;
    }

    public String getLabelWithoutBalancing() {
        return classifier + "_" + featureSelection + "_" + costSensitive;
    }
}