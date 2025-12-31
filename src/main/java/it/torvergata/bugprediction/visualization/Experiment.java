package it.torvergata.bugprediction.visualization;

public class Experiment {

    private final String classifier;
    private final String featureSelection;
    private final String balancing;
    private final String costSensitive;
    private final double f1;
    private final double auc;
    private final double mcc;
    private final double npofB25;

    public Experiment(String classifier, String featureSelection, String balancing,
                      String costSensitive, double f1, double auc, double mcc, double npofB25) {
        this.classifier = classifier;
        this.featureSelection = featureSelection;
        this.balancing = balancing;
        this.costSensitive = costSensitive;
        this.f1 = f1;
        this.auc = auc;
        this.mcc = mcc;
        this.npofB25 = npofB25;
    }

    public String getClassifier() { return classifier; }
    public String getBalancing() { return balancing; }

    public double getF1() { return f1; }
    public double getAuc() { return auc; }
    public double getMcc() { return mcc; }
    public double getNpofB25() { return npofB25; }

    public String getLabelWithoutBalancing() {
        String csShort = costSensitive.equalsIgnoreCase("noCostSensitive") ? "noCS" :
                costSensitive.equalsIgnoreCase("yesCostSensitive") ? "yesCS" :
                        costSensitive; // fallback, nel caso compaiano altri valori

        return classifier + "_" + featureSelection + "_" + csShort;
    }

}