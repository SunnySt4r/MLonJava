package org.example;

import org.apache.lucene.analysis.ru.RussianAnalyzer;
import smile.classification.KNN;
import smile.classification.SVM;
import smile.data.DataFrame;
import smile.data.SparseDataset;
import smile.data.vector.BaseVector;
import smile.data.vector.IntVector;
import smile.feature.extraction.BagOfWords;
import smile.io.Read;
import smile.math.matrix.Matrix;
import smile.math.matrix.SparseMatrix;
import smile.plot.swing.Histogram;
import smile.plot.swing.Histogram3D;
import smile.plot.swing.Plot;
import smile.plot.swing.PlotGrid;
import smile.stat.distribution.BernoulliDistribution;
import smile.validation.metric.AUC;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;
import weka.core.Stopwords;
import weka.core.pmml.jaxbbindings.ROC;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;


public class App {

    static Map<String, Integer> dictionary = new HashMap<>();
    public static Set<String> positive = new HashSet<>();
    public static Set<String> negative = new HashSet<>();
    static Set<String> del = new HashSet<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main( String[] args ) throws IOException, URISyntaxException, InterruptedException, InvocationTargetException {
        DataFrame df = Read.csv("src/main/resources/train.csv");
        DataFrame test = Read.csv("src/main/resources/test.csv");
        System.out.println(df.size());

        RussianAnalyzer russianAnalyzer = new RussianAnalyzer();
        System.out.println(russianAnalyzer.getStopwordSet());

        for (String s : df.column("V2").toStringArray()) {
            s = s.replaceAll("[^\u0400-\u04FF]", " ").replaceAll("[ \\t]{2,}", " ").toLowerCase();
            boolean prevIsNot = false;
            for (String w : s.split(" ")) {

                if(w.equals("не")){
                    prevIsNot = true;
                    continue;
                }
                if(prevIsNot && !dictionary.containsKey("не " + w)){
                    prevIsNot = false;
//                    w = "не " + w;
//                    System.out.println("не " + w);
//                    int i = scanner.nextInt();
//                    if(i==1){
//                        positive.add("не " + w);
//                        negative.add(w);
//                    }else if(i==2){
//                        negative.add("не " + w);
//                        positive.add(w);
//                    }
                    dictionary.put("не " + w, 1);
                }
            }
        }
        System.out.println(positive);
        System.out.println("-------------");
        System.out.println(negative);


//        BagOfWords bagOfWords = BagOfWords.fit(df, s -> s
//                .replaceAll("[^\u0400-\u04FF]", " ")
//                .replaceAll("[ \\t]{2,}", " ")
//                .toLowerCase().split(" "), 1000, "V2");
//        System.out.println(Arrays.toString(bagOfWords.features()));
//        System.out.println(Arrays.toString(bagOfWords.apply(df.get(2, "V2").toString())));
    }
}
