package org.example;

public enum SentimentClass {
    NEGATIVE(0),
    POSITIVE(1),
    NEUTRAL(0.5);

    private final double score;

    SentimentClass(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

}
