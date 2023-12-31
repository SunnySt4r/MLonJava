package org.example.competition2.threeway;


import org.example.competition2.SentimentClass;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.io.Read;
import smile.io.Write;

import java.nio.file.Paths;

public class ThreeWayMNBTrainerRunner {
    public static void main(String[] args) throws Exception {
        DataFrame train = Read.csv("src/main/resources/changed/train.csv");
        DataFrame test = Read.csv("src/main/resources/changed/test.csv");

        String outputModel = "src/main/resources/models/sentiment.model";

        ThreeWayMNBTrainer threeWayMNBTrainer;

        System.out.println("Adding training instances");
        int k = 10;

        DataFrame random =  train.stream().skip(1)
                .map(tuple -> {
                    String s = tuple.getString("V2");
                    Tuple res = Tuple.of(new Object[]{tuple.get("V1") , (s != null)? s : "", tuple.get("V3"), (int) (Math.random()*5)},
                            new StructType(
                                    new StructField("id", DataTypes.IntegerObjectType),
                                    new StructField("text", DataTypes.StringType),
                                    new StructField("sentiment", DataTypes.IntegerObjectType),
                                    new StructField("tier", DataTypes.IntegerType)));

                    return res;
                }).collect(DataFrame.Collectors.collect());

        DataFrame test1 = test.stream().skip(1)
                .map(tuple -> {
                    String s = tuple.getString("V3");
                    Tuple res = Tuple.of(new Object[]{tuple.get("V1"), ((s!=null)? s : ""), 0},
                            new StructType(
                                    new StructField("id", DataTypes.IntegerObjectType),
                                    new StructField("text", DataTypes.StringType),
                                    new StructField("sentiment", DataTypes.DoubleObjectType)));

                    return res;
                }).collect(DataFrame.Collectors.collect());;

        for(int i=0; i<k; i++){
            int finalI = i;
            threeWayMNBTrainer = new ThreeWayMNBTrainer(outputModel);
            ThreeWayMNBTrainer finalThreeWayMNBTrainer = threeWayMNBTrainer;
            DataFrame temp =  random.stream()
                    .map(tuple -> {
                        if(!tuple.get("tier").equals(finalI)){
                            SentimentClass sentiment;
                            String s = tuple.getString("text");
                            String[] words = ((s != null)? s : "").split(" ");
                            if(tuple.get("sentiment").toString().equals("1")){
                                sentiment = SentimentClass.POSITIVE;
                                finalThreeWayMNBTrainer.addTrainingInstance(sentiment, words);
                            }else{
                                sentiment = SentimentClass.NEGATIVE;
                            }
                            finalThreeWayMNBTrainer.addTrainingInstance(sentiment, words);
                        }
                        return tuple;
                    }).collect(DataFrame.Collectors.collect());

            System.out.println("Training and saving Model " + i);
            threeWayMNBTrainer.trainModel();
            threeWayMNBTrainer.testModel();

            test1 = test1.stream()
                    .map(tuple -> {
                        String s = tuple.getString("text");
                        double sentiment;
                        try {
                            sentiment = finalThreeWayMNBTrainer.classify(s).getScore()/k;
                        } catch (Exception e) {
                            sentiment = 0.5/k;
                        }

                        Tuple res = Tuple.of(new Object[]{tuple.get("id"), ((s!=null)? s : ""), tuple.getDouble("sentiment") + sentiment},
                                new StructType(
                                        new StructField("id", DataTypes.IntegerObjectType),
                                        new StructField("text", DataTypes.StringType),
                                        new StructField("sentiment", DataTypes.DoubleObjectType)));

                        return res;
                    }).collect(DataFrame.Collectors.collect());
        }

        System.out.println(test1);
        test1 = test1.stream()
                        .map(tuple -> {
                            Tuple res = Tuple.of(new Object[]{tuple.get("id"), tuple.getDouble("sentiment")},
                                    new StructType(
                                            new StructField("id", DataTypes.IntegerObjectType),
                                            new StructField("sentiment", DataTypes.DoubleObjectType)));
                            return res;
                        }).collect(DataFrame.Collectors.collect());
        Write.csv(test1, Paths.get("src/main/resources/res/test.csv"));
    }
}
