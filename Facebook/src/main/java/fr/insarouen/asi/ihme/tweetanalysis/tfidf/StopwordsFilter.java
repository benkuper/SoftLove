package fr.insarouen.asi.ihme.tweetanalysis.tfidf;

import java.io.*;
import java.util.*;

public class StopwordsFilter {

    public static final String STOPWORDS_FILENAME = "stopwords";

    public static List<WeightedWord> filter(List<WeightedWord> words) {
        try {
            List<String> stopWords = new ArrayList<String>();
            List<WeightedWord> removeList = new LinkedList<WeightedWord>();
            File file = new File(STOPWORDS_FILENAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while( (line = br.readLine())!= null ){
                stopWords.add(line.trim());
            }
            for(WeightedWord word : words) {
                for(String stopWord : stopWords) {
                    if(word.getWord().equalsIgnoreCase(stopWord)) {
                        removeList.add(word);
                        break;
                    }
                }
            }
            for(WeightedWord i : removeList) {
                words.remove(i);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("STOPWORDS FILTER : failed to filter.");
        } finally {
            return words;
        }
    }
}
