package fr.insarouen.asi.ihme.tweetanalysis.tfidf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TFIDFCalculator implements ScoreCalculator {

    public static double termFrequency(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    public static double inverseTermFrequency(List<List<String>> docs, String term) {
        double n = 0;

        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }

        return Math.log(docs.size() / n);
    }

    public static double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return termFrequency(doc, term) * inverseTermFrequency(docs, term);
    }

    public static List<List<String>> makeStrListList(String... sources) {
        List<List<String>> strListList = new LinkedList<List<String>>();
        for(int i = 0; i < sources.length; i++) {
            List<String> splitList = Arrays.asList(sources[i].split(" "));
            strListList.add(splitList);
        }
        return strListList;
    }

    public static List<String> makeWordList(String... sources) {
        List<String> wordList = new LinkedList<String>();
        for(int i = 0; i < sources.length; i++) {
            List<String> splitList = Arrays.asList(sources[i].split(" "));
            for(String word : splitList) {
                if(!wordList.contains(word)) {
                    wordList.add(word);
                }
            }
        }
        return wordList;
    }

    public List<WeightedWord> getScores(String... sources) {
        List<WeightedWord> words = new LinkedList<WeightedWord>();
        List<String> simpleWords = makeWordList(sources);
        List<List<String>> docList = makeStrListList(sources);
        for(String word : simpleWords) {
            double score = 0;
            for(List<String> doc : docList) {
                if(doc.contains(word)) {
                    score += tfIdf(doc, docList, word);
                }
            }
            words.add(new WeightedWord(word, score));
        }
        return words;
    }

    public static void main(String[] args) {

        List<String> doc1 = Arrays.asList("Lorem", "ipsum", "dolor", "ipsum", "sit", "ipsum");
        List<String> doc2 = Arrays.asList("Vituperata", "incorrupte", "at", "ipsum", "pro", "quo");
        List<String> doc3 = Arrays.asList("Has", "persius", "disputationi", "id", "simul");
        List<List<String>> documents = Arrays.asList(doc1, doc2, doc3);

        TFIDFCalculator calculator = new TFIDFCalculator();
        double tfidf = calculator.tfIdf(doc1, documents, "ipsum");

        System.out.println("TF-IDF (ipsum) = " + tfidf);
    }

}