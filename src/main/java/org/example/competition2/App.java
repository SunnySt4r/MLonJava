package org.example.competition2;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import smile.classification.Classifier;
import smile.classification.GradientTreeBoost;
import smile.classification.KNN;
import smile.classification.SVM;
import smile.data.DataFrame;
import smile.data.SparseDataset;
import smile.data.Tuple;
import smile.data.type.*;
import smile.data.vector.BaseVector;
import smile.data.vector.IntVector;
import smile.feature.extraction.BagOfWords;
import smile.io.Read;
import smile.io.Write;
import smile.math.Function;
import smile.math.distance.Distance;
import smile.math.distance.ManhattanDistance;
import smile.math.distance.MinkowskiDistance;
import smile.math.kernel.GaussianKernel;
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
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.core.DistanceFunction;
import weka.core.Instances;
import weka.core.Stopwords;
//import weka.core.pmmlROC;
//import weka.core.pmml.jaxbbindings.SupportVectorMachine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;


public class App {

    static Map<String, String> dictionary = new HashMap<>();

    public static void main( String[] args ) throws IOException, URISyntaxException{
        DataFrame train = Read.csv("src/main/resources/competition2/changed/train.csv");
        DataFrame test = Read.csv("src/main/resources/competition2/changed/test.csv");
        BagOfWords bagOfWords = BagOfWords.fit(train, s -> s.split(" "), 1000, "V2");

        DataFrame word2vec = train.stream().skip(2)
                .map(tuple -> {
                    String s = tuple.getString("V2");
                    Tuple res = Tuple.of(new Object[]{tuple.get("V1") , bagOfWords.apply((s != null)? s : ""), tuple.get("V3")},
                            new StructType(
                                    new StructField("id", DataTypes.IntegerObjectType),
                                    new StructField("text_vec", DataTypes.IntegerArrayType),
                                    new StructField("sentiment", DataTypes.IntegerObjectType)));

                    return res;
                })
                .collect(DataFrame.Collectors.collect());

        DataFrame test2vec = test.stream().skip(2)
                .map(tuple -> {
                    String s = tuple.getString("V3");
                    Tuple res = Tuple.of(new Object[]{tuple.get("V1") , bagOfWords.apply((s != null)? s : " ")},
                            new StructType(
                                    new StructField("id", DataTypes.IntegerObjectType),
                                    new StructField("text_vec", DataTypes.IntegerArrayType)));

                    return res;
                })
                .collect(DataFrame.Collectors.collect());


        System.out.println(word2vec.toString());
        System.out.println(word2vec.size());
        System.out.println(train.size());


//        Map<String, Integer> map = new HashMap<>();
//        for(int i=0; i<word2vec.size(); i++){
//            map.put(word2vec.get(i).get("sentiment").toString(), 1);
//        }
//        System.out.println(map);System.out.println(Arrays.toString(x[0]));
        int[][] x = new int[word2vec.size()][bagOfWords.features().length];
        int[] y = new int[word2vec.size()];

        for(int i=0; i<word2vec.size(); i++){
            x[i] = (int[]) word2vec.get(i).get("text_vec");
            y[i] = Integer.parseInt(word2vec.get(i).get("sentiment").toString()) * 2 - 1;
        }

//        Classifier<int[]> model = SVM.fit(x, y, 1, 0.1, 0.01);

//        DataFrame result = test2vec.stream().skip(2)
//                .map(tuple -> {
//                    Tuple res = Tuple.of(new Object[]{tuple.get("id") , model.predict((double[]) tuple.get("text_vec"))},
//                            new StructType(
//                                    new StructField("id", DataTypes.IntegerObjectType),
//                                    new StructField("sentiment", DataTypes.IntegerObjectType)));
//
//                    return res;
//                })
//                .collect(DataFrame.Collectors.collect());
//
//        Write.csv(result, Paths.get("src/main/resources/competition2/changed/result.csv"));


//        word2vec = DataFrame.
//        word2vec = word2vec.omitNullRows();

//        System.out.println(Arrays.toString(bagOfWords.features()));
//        System.out.println(word2vec.summary());

//        System.out.println();
//        System.out.println(dictionary.size());

    }
}
