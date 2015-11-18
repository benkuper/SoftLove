package fr.insarouen.asi.ihme.tweetanalysis.tfidf;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimpleScoreCalculator implements ScoreCalculator {

    public List<WeightedWord> getScores(String... sources) {
        ArrayList<WeightedWord> wwords = new ArrayList<WeightedWord>();
        ArrayList<String> words = new ArrayList<String>();
        int index, i, j;
        String currentWord;
        WeightedWord currWWord;
        for(i = 0; i < sources.length; i++) {
            String[] src = sources[i].split(" ");
            for(j = 0; j < src.length; j ++) {
                currentWord = src[j].toLowerCase();
                index = words.indexOf(currentWord);
                if(index == -1) {
                    words.add(currentWord);
                    wwords.add(new WeightedWord(currentWord, 1));
                } else {
                    currWWord = wwords.get(index);
                    currWWord.setScore(currWWord.getScore() + 1);
                }
            }
        }
        return wwords;
    }
}