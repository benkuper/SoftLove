package fr.insarouen.asi.ihme.tweetanalysis.tfidf;

public class WeightedWord {
    private String word;
    private double score;

    public WeightedWord(String word) {
        this.word = word;
        this.score = 0;
    }

    public WeightedWord(String word, double score) {
        this.word = word;
        this.score = score;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getWord() {
        return this.word;
    }

    public double getScore() {
        return this.score;
    }

}