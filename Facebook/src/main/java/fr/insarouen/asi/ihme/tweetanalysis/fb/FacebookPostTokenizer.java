package fr.insarouen.asi.ihme.tweetanalysis.fb;

import java.io.*;
import java.text.*;
import java.util.*;

public class FacebookPostTokenizer {
    
    public static List<String> tokenize(String... posts) {
        List<String> tokens = new ArrayList<String>();
        for(int i = 0; i < posts.length; i++) {
            BreakIterator bi = BreakIterator.getWordInstance();
            bi.setText(posts[i]);
            int begin = bi.first();
            int end;
            for (end = bi.next(); end != BreakIterator.DONE; end = bi.next()) {
                String t = posts[i].substring(begin, end);
                if (t.trim().length() > 0) {
                    tokens.add(posts[i].substring(begin, end));
                }
                begin = end;
            }
            if (end != -1) {
                tokens.add(posts[i].substring(end));
            }
        }
        return tokens;
    }
}

