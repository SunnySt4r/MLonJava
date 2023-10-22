package org.example.threeway;

import org.example.SentimentClass;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class KaggleCSVWriterThreeWay {
    public static final String CSV_HEADER = "PhraseId,Sentiment";
    BufferedWriter bw;

    public KaggleCSVWriterThreeWay(String csvFile) throws IOException {
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "utf8"));
        bw.write(CSV_HEADER);
        bw.write("\n");
    }

    public void close() throws IOException {
        bw.close();
    }
}
