package fr.insa.asi.ihme.chatbot.knowledge;

import java.util.Vector;

/**
 * Created by clement on 07/12/2015.
 */
public class PrePostList extends Vector{
    /**
     *  Add another entry to the list.
     */
    public void add(String src, String dest) {
        addElement(new PrePost(src, dest));
    }

    /**
     *  Prnt the pre-post list.
     */
    public void print(int indent) {
        for (int i = 0; i < size(); i++) {
            PrePost p = (PrePost)elementAt(i);
            p.print(indent);
        }
    }

    /**
     *  Translate a string.
     *  If str matches a src string on the list,
     *  return he corresponding dest.
     *  If no match, return the input.
     */
    String xlate(String str) {
        for (int i = 0; i < size(); i++) {
            PrePost p = (PrePost)elementAt(i);
            if (str.equals(p.src())) {
                return p.dest();
            }
        }
        return str;
    }

    /**
     *  Translate a string s.
     *  (1) Trim spaces off.
     *  (2) Break s into words.
     *  (3) For each word, substitute matching src word with dest.
     */
    public String translate(String s) {
        String lines[] = new String[2];
        String work = MString.trim(s);
        s = "";
        while (MString.match(work, "* *", lines)) {
            s += xlate(lines[0]) + " ";
            work = MString.trim(lines[1]);
        }
        s += xlate(work);
        return s;
    }
}
