package org.example.threeway;

import com.google.inject.internal.util.Join;
import org.example.SentimentClass;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.tokenizers.WordTokenizer;

import java.util.ArrayList;
import java.util.Random;

public class ThreeWayMNBTrainer {
    private NaiveBayesMultinomialText classifier;
    private String modelFile;
    private Instances dataRaw;

    public ThreeWayMNBTrainer(String outputModel) {
        classifier = new NaiveBayesMultinomialText();
        modelFile = outputModel;

        ArrayList<Attribute> atts = new ArrayList<>(2);
        ArrayList<String> classVal = new ArrayList<>();
        classVal.add(SentimentClass.NEGATIVE.name());
        classVal.add(SentimentClass.POSITIVE.name());
        atts.add(new Attribute("content",(ArrayList<String>)null));
        atts.add(new Attribute("@@class@@",classVal));

        dataRaw = new Instances("TrainingInstances",atts,10);
        dataRaw.setClassIndex(1);
    }

    public void addTrainingInstance(SentimentClass sentiment, String[] words) {
        double[] instanceValue = new double[dataRaw.numAttributes()];
        instanceValue[0] = dataRaw.attribute(0).addStringValue(Join.join(" ", words));
        instanceValue[1] = sentiment.getScore();
        dataRaw.add(new DenseInstance(1.0, instanceValue));
        dataRaw.setClassIndex(1);
    }

    public void trainModel() throws Exception {
        classifier.buildClassifier(dataRaw);
    }

    public void testModel() throws Exception {
        Evaluation eTest = new Evaluation(dataRaw);
        eTest.evaluateModel(classifier, dataRaw);
        System.out.println(eTest.areaUnderROC(1));
        System.out.println(eTest.errorRate());
    }

    public void showInstances() {
        System.out.println(dataRaw);
    }

    public Instances getDataRaw() {
        return dataRaw;
    }

    public void saveModel() throws Exception {
        weka.core.SerializationHelper.write(modelFile, classifier);
    }

    public void loadModel(String _modelFile) throws Exception {
        NaiveBayesMultinomialText classifier = (NaiveBayesMultinomialText) weka.core.SerializationHelper.read(_modelFile);
        this.classifier = classifier;
    }

    public SentimentClass classify(String sentence) throws Exception {
        double[] instanceValue = new double[dataRaw.numAttributes()];
        instanceValue[0] = dataRaw.attribute(0).addStringValue(sentence);

        Instance toClassify = new DenseInstance(1.0, instanceValue);
        dataRaw.setClassIndex(1);
        toClassify.setDataset(dataRaw);

        double prediction = this.classifier.classifyInstance(toClassify);

        double distribution[] = this.classifier.distributionForInstance(toClassify);

        if (distribution[0] != distribution[1])
            return SentimentClass.values()[(int)prediction];
        else
            return SentimentClass.NEUTRAL;
    }
}
