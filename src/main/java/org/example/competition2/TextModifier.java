package org.example.competition2;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArrayMap;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.StructType;
import smile.io.Read;
import smile.io.Write;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

public class TextModifier {
    public static void main(String[] args) throws IOException, URISyntaxException {
        DataFrame df = Read.csv("src/main/resources/competition2/train.csv");
        DataFrame test = Read.csv("src/main/resources/competition2/test.csv");

        DataFrame changedTrain = changeAllStrings(df, true);
        DataFrame changedTest = changeAllStrings(test, false);


        Write.csv(changedTrain, Paths.get("src/main/resources/competition2/changed/train.csv"));
        Write.csv(changedTest, Paths.get("src/main/resources/competition2/changed/test.csv"));
    }

    private static DataFrame changeAllStrings(DataFrame df, boolean isTrain){
        String column = isTrain? "V2" : "V3";

        final List<Tuple> list = new ArrayList<>();

        DataFrame updatedDf = df.stream()
                .map(tuple -> {
                    String s = tuple.getString(column);
                    s = removeNeedless(s);
                    s = applyNot(s);
                    s = s.replaceAll("\\s.\\s", " ")
                            .replaceAll("[ \\t]{2,}", " ");
                    Tuple res;
                    if(isTrain){
                        res = Tuple.of(new Object[]{tuple.get("V1"), s.trim(), tuple.get("V3")}, df.schema());
                    }else {
                        res = Tuple.of(new Object[]{tuple.get("V1"), tuple.get("V2"), s.trim()}, df.schema());
                    }
                    list.add(res);

                    return res;
                })
                .collect(DataFrame.Collectors.collect());
        List<Tuple> list2 = updatedDf.toList();
        list2.remove(0);

        return DataFrame.of(list2);
    }

    private static String removeNeedless(String s){
        s = s.replaceAll("[^\u0400-\u04FF]", " ")
                .replaceAll("[ \\t]{2,}", " ")
                .toLowerCase();
        return s;
    }

    private static String applyNot(String s){
        s = s.replaceAll(" не ", " не_")
                .replaceAll(" никогда ", " никогда_")
                .replaceAll(" ни ", " ни_")
                .replaceAll(" нет ", " нет_")
                .replaceAll("^не ", "не_")
                .replaceAll("^никогда ", "никогда_")
                .replaceAll("^ни ", "ни_")
                .replaceAll("^нет ", "нет_");
        return s;
    }

    private static String analyze(String s) throws IOException {
        Analyzer analyzer = new RussianAnalyzer();
        TokenStream stream = analyzer.tokenStream("field", s);
        StringBuilder res = new StringBuilder();
        stream.reset();
        while (stream.incrementToken()) {
            res.append(stream.getAttribute(CharTermAttribute.class).toString()).append(" ");
        }
        stream.end();
        stream.close();
        return res.toString();
    }

    //10816,...........,1
    //10742,......,0
    //16161,45666059,1
    //крайне интересные строчки в train
}
