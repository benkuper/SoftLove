package fr.insa.asi.ihme.chatbot;


import fr.insa.asi.ihme.chatbot.knowledge.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatBot {
    String[][] language={
            //standard greetings
            {"salut","yop","bonjour","comment va","hey"},
            {"bonjour","salut"},
            //question greetings
            {"comment ça va","comment vas-tu","bien","bien la famille"},
            {"Bien.","Ca va.", "Tres bien, merci. Et toi ?"},
            //yes
            {"oui", "si"},
            {"non.","NON !","Non merci."},
            //non
            {"non"},
            {"Et pourtant !", "Si.", "J'y peux rien !"},
            //default
            {"Je n'ai pas compris, peux-tu reformuler ?"}
    };
    final boolean echoInput = false;
    final boolean printData = false;

    final boolean printKeys = false;
    final boolean printSyns = false;
    final boolean printPrePost = false;
    final boolean printInitialFinal = false;

    /** The key list */
    KeyList keys = new KeyList();
    /** The syn list */
    SynList syns = new SynList();
    /** The pre list */
    PrePostList pre = new PrePostList();
    /** The post list */
    PrePostList post = new PrePostList();
    /** Initial string */
    String initial = "Hello.";
    /** Final string */
    String finl = "Goodbye.";
    /** Quit list */
    WordList quit = new WordList();

    /** Key stack */
    KeyStack keyStack = new KeyStack();

    /** Memory */
    Mem mem = new Mem();

    DecompList lastDecomp;
    ReasembList lastReasemb;
    boolean finished = false;

    static final int success = 0;
    static final int failure = 1;
    static final int gotoRule = 2;


    public ChatBot(){

    }
    public boolean finished() {
        return finished;
    }
    public void collect(String s) {
        String lines[] = new String[4];

        if (MString.match(s, "*reasmb: *", lines)) {
            if (lastReasemb == null) {
                System.out.println("Error: no last reasemb");
                return;
            }
            lastReasemb.add(lines[1]);
        }
        else if (MString.match(s, "*decomp: *", lines)) {
            if (lastDecomp == null) {
                System.out.println("Error: no last decomp");
                return;
            }
            lastReasemb = new ReasembList();
            String temp = new String(lines[1]);
            if (MString.match(temp, "$ *", lines)) {
                lastDecomp.add(lines[0], true, lastReasemb);
            } else {
                lastDecomp.add(temp, false, lastReasemb);
            }
        }
        else if (MString.match(s, "*key: * #*", lines)) {
            lastDecomp = new DecompList();
            lastReasemb = null;
            int n = 0;
            if (lines[2].length() != 0) {
                try {
                    n = Integer.parseInt(lines[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Number is wrong in key: " + lines[2]);
                }
            }
            keys.add(lines[1], n, lastDecomp);
        }
        else if (MString.match(s, "*key: *", lines)) {
            lastDecomp = new DecompList();
            lastReasemb = null;
            keys.add(lines[1], 0, lastDecomp);
        }
        else if (MString.match(s, "*synon: * *", lines)) {
            WordList words = new WordList();
            words.add(lines[1]);
            s = lines[2];
            while (MString.match(s, "* *", lines)) {
                words.add(lines[0]);
                s = lines[1];
            }
            words.add(s);
            syns.add(words);
        }
        else if (MString.match(s, "*pre: * *", lines)) {
            pre.add(lines[1], lines[2]);
        }
        else if (MString.match(s, "*post: * *", lines)) {
            post.add(lines[1], lines[2]);
        }
        else if (MString.match(s, "*initial: *", lines)) {
            initial = lines[1];
        }
        else if (MString.match(s, "*final: *", lines)) {
            finl = lines[1];
        }
        else if (MString.match(s, "*quit: *", lines)) {
            quit.add(" " + lines[1]+ " ");
        }
        else {
            System.out.println("Unrecognized input: " + s);
        }
    }
    /**
     *  Assembly a reply from a decomp rule and the input.
     *  If the reassembly rule is goto, return null and give
     *    the gotoKey to use.
     *  Otherwise return the response.
     */
    String assemble(Decomp d, String reply[], Key gotoKey) {
        String lines[] = new String[3];
        d.stepRule();
        String rule = d.nextRule();
        if (MString.match(rule, "goto *", lines)) {
            //  goto rule -- set gotoKey and return false.
            gotoKey.copy(keys.getKey(lines[0]));
            if (gotoKey.key() != null) return null;
            System.out.println("Goto rule did not match key: " + lines[0]);
            return null;
        }
        String work = "";
        while (MString.match(rule, "* (#)*", lines)) {
            //  reassembly rule with number substitution
            rule = lines[2];        // there might be more
            int n = 0;
            try {
                n = Integer.parseInt(lines[1]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Number is wrong in reassembly rule " + lines[1]);
            }
            if (n < 0 || n >= reply.length) {
                System.out.println("Substitution number is bad " + lines[1]);
                return null;
            }
            reply[n] = post.translate(reply[n]);
            work += lines[0] + " " + reply[n];
        }
        work += rule;
        if (d.mem()) {
            mem.save(work);
            return null;
        }
        return work;
    }
    /**
     *  Decompose a string according to the given key.
     *  Try each decomposition rule in order.
     *  If it matches, assemble a reply and return it.
     *  If assembly fails, try another decomposition rule.
     *  If assembly is a goto rule, return null and give the key.
     *  If assembly succeeds, return the reply;
     */
    String decompose(Key key, String s, Key gotoKey) {
        String reply[] = new String[10];
        for (int i = 0; i < key.decomp().size(); i++) {
            Decomp d = (Decomp)key.decomp().elementAt(i);
            String pat = d.pattern();
            if (syns.matchDecomp(s, pat, reply)) {
                String rep = assemble(d, reply, gotoKey);
                if (rep != null) return rep;
                if (gotoKey.key() != null) return null;
            }
        }
        return null;
    }
    /**
     *  Process a sentence.
     *  (1) Make pre transformations.
     *  (2) Check for quit word.
     *  (3) Scan sentence for keys, build key stack.
     *  (4) Try decompositions for each key.
     */
    String sentence(String s) {
        s = pre.translate(s);
        s = MString.pad(s);
        if (quit.find(s)) {
            finished = true;
            return finl;
        }
        keys.buildKeyStack(keyStack, s);
        for (int i = 0; i < keyStack.keyTop(); i++) {
            Key gotoKey = new Key();
            String reply = decompose(keyStack.key(i), s, gotoKey);
            if (reply != null) return reply;
            //  If decomposition returned gotoKey, try it
            while (gotoKey.key() != null) {
                reply = decompose(gotoKey, s, gotoKey);
                if (reply != null) return reply;
            }
        }
        return null;
    }
    void clearScript() {
        keys.clear();
        syns.clear();
        pre.clear();
        post.clear();
        initial = "";
        finl = "";
        quit.clear();
        keyStack.reset();
    }
    /**
     *  Process a line of input.
     */
    public String processInput(String s) {
        String reply;
        //  Do some input transformations first.
        s = MString.translate(s, "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "abcdefghijklmnopqrstuvwxyz");
        s = MString.translate(s, "@#$%^&*()_-+=~`{[}]|:;<>\\\"",
                "                          "  );
        s = MString.translate(s, ",?!", "...");
        //  Compress out multiple speace.
        s = MString.compress(s);
        String lines[] = new String[2];
        //  Break apart sentences, and do each separately.
        while (MString.match(s, "*.*", lines)) {
            reply = sentence(lines[0]);
            if (reply != null) return reply;
            s = MString.trim(lines[1]);
        }
        if (s.length() != 0) {
            reply = sentence(s);
            if (reply != null) return reply;
        }
        //  Nothing matched, so try memory.
        String m = mem.get();
        if (m != null) return m;

        //  No memory, reply with xnone.
        Key key = keys.getKey("xnone");
        if (key != null) {
            Key dummy = null;
            reply = decompose(key, s, dummy);
            if (reply != null) return reply;
        }
        //  No xnone, just say anything.
        return "I am at a loss for words.";
    }
    public boolean readDefaultScript()	{
        clearScript();
        return readScript("mati.script");
    }
    public boolean readScript(String script) {
        clearScript();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(script));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String str;
        List<String> list = new ArrayList<String>();
        try {
            int cpt =0;
            while((str = in.readLine()) != null){
                list.add(str);
                cpt++;
            }
            System.out.println("nb lines : " + cpt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] lines = list.toArray(new String[0]);
        if (lines == null || lines.length == 0) {
            System.err.println("Cannot load Eliza script!");
            return false;
        } else {
            for (int i = 0; i < lines.length; i++) {
                collect(lines[i]);
            }
            return true;
        }
    }
    /**
     *  Print the stored script.
     */
    public void print() {
        if (printKeys) keys.print(0);
        if (printSyns) syns.print(0);
        if (printPrePost) {
            pre.print(0);
            post.print(0);
        }
        if (printInitialFinal) {
            System.out.println("initial: " + initial);
            System.out.println("final:   " + finl);
            quit.print(0);
            quit.print(0);
        }
    }
    public String getResponse(String quote){
        String res = "" +
                "";
        quote.trim();
        while (
                quote.charAt(quote.length() - 1) == '!' ||
                        quote.charAt(quote.length() - 1) == '.' ||
                        quote.charAt(quote.length() - 1) == ' ' ||
                        quote.charAt(quote.length() - 1) == '?'
                ) {
            quote = quote.substring(0, quote.length() - 1);
        }
        quote.trim();
        quote = quote.toLowerCase();
        byte response = 0;
			/*
			0:we're searching through chatBot[][] for matches
			1:we didn't find anything
			2:we did find something
			*/
        //-----check for matches----
        int j = 0;//which group we're checking
        while (response == 0) {
            if (inArray(quote.toLowerCase(), language[j * 2])) {
                response = 2;
                int r = (int) Math.floor(Math.random() * language[(j * 2) + 1].length);
                res = language[(j * 2) + 1][r];
            }
            j++;
            if (((j * 2) == (language.length - 1)) && (response == 0)) {
                response = 1;
            }
        }

        //-----default--------------
        if (response == 1) {
            int r = (int) Math.floor(Math.random() * language[language.length - 1].length);
            res = language[language.length - 1][r];
        }
        return res;
    }
    public boolean inArray(String in,String[] str){
        boolean match=false;
        for(int i=0;i<str.length;i++){
            if(str[i].equals(in)){
                match=true;
            }
        }
        return match;
    }

}