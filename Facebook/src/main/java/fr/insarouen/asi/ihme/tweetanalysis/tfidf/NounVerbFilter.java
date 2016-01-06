package fr.insarouen.asi.ihme.tweetanalysis.tfidf;

import java.io.*;
import java.util.*;
import org.annolab.tt4j.*;

public class NounVerbFilter {

    public static final List<String> wordPosAccept = Arrays.asList(new String[]{
        "NAM", "NOM"
    });

    public static List<String> filter(List<String> words) {
        TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
        final List<String> results = new ArrayList<String>();
        try {
            tt.setModel("french.par");
            tt.setHandler(new TokenHandler<String>() {
                public void token(String token, String pos, String lemma) {
                    if (lemma.length()>2){
                        if(wordPosAccept.contains(pos)) {
                            results.add(lemma.toLowerCase());
                        }
                    }
                }
            });
            tt.process(words);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            tt.destroy();
            return results;
        }
    }
}
