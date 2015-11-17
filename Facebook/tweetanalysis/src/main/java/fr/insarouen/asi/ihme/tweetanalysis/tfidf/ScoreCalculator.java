package fr.insarouen.asi.ihme.tweetanalysis.tfidf;

import java.util.List;

public interface ScoreCalculator {
    List<WeightedWord> getScores(String... sources);
}