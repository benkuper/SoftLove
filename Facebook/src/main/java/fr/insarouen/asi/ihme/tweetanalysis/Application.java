package fr.insarouen.asi.ihme.tweetanalysis;

import fr.insarouen.asi.ihme.tweetanalysis.fb.*;
import fr.insarouen.asi.ihme.tweetanalysis.tfidf.*;
import fr.insarouen.asi.ihme.tweetanalysis.zmq.*;

import java.util.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;
import java.awt.Desktop;
import java.net.URI;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;


public class Application implements Observer {
    public Application(){
    }

    public void doTraitement(String code){
            fbConnector.connect(code);
            System.out.println();
            try {
                System.out.println(fbConnector.getPostLiked());
                System.out.println(fbConnector.getUserprofilePicture());
            } catch(Exception ex) {
                System.out.println("Error while getting the user likes and profile picture.");
            }

            System.out.println();
            String keywords = "";
            try {
                System.out.println("Trying to TFIDF your liked posts...");
                String[] data = (fbConnector.getLikedPosts());
                System.out.println("TFIDFing...");
                List<String> rawWords = FacebookPostTokenizer.tokenize(data);
                List<String> lemmas = NounVerbFilter.filter(rawWords);
                String[] lemmasArray = lemmas.toArray(new String[0]);
                List<WeightedWord> wwords = StopwordsFilter.filter(new TFIDFCalculator().getScores(lemmasArray));

                // list sorting
                Collections.sort(wwords, new Comparator<WeightedWord>() {
                    public int compare(WeightedWord w1, WeightedWord w2) {
                        return (-1) * new Double(w1.getScore()).compareTo(new Double(w2.getScore()));
                    }
                });

                System.out.println("Done. Here's a top 10");
                keywords = wordsToList(wwords);
                this.sender.send(ZMQTOPIC_FBKEYWORDS, keywords);
                this.sender.send(ZMQTOPIC_FBPICTURE,  fbConnector.getUserprofilePicture());

            } catch(Exception ex) {
                System.out.println("Well this didn't work for some reason. Maybe there's nothing to get.");
            }


    }

    public void setObserver() throws FileNotFoundException, IOException, ParseException{
        try{
            FileReader reader = new FileReader("config.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            zmqrFbLikes = new ZMQReceiver(Integer.parseInt((String) jsonObject.get("FBLIKES_SUBSCRIBER_PORT")), ZMQTOPIC_FBLIKES);
            zmqrFbPicture = new ZMQReceiver(Integer.parseInt((String) jsonObject.get("FBPICTURE_SUBSCRIBER_PORT")), ZMQTOPIC_FBPICTURE);
            zmqrFbKeywords = new ZMQReceiver(Integer.parseInt((String) jsonObject.get("FBKEYWORD_SUBSCRIBER_PORT")), ZMQTOPIC_FBKEYWORDS);
            zmqrFbDefault = new ZMQReceiver(Integer.parseInt((String) jsonObject.get("FBDATA_SUBSCRIBER_PORT")), ZMQTOPIC_FBDEFAULT);

            zmqrFbLikes.addObserver(this);
            zmqrFbPicture.addObserver(this);
            zmqrFbKeywords.addObserver(this);
            zmqrFbDefault.addObserver(this);

            String keywords = "";

            String[] data = (fbConnector.getLikedPosts());
            List<String> rawWords = FacebookPostTokenizer.tokenize(data);
            List<String> lemmas = NounVerbFilter.filter(rawWords);
            String[] lemmasArray = lemmas.toArray(new String[0]);
            List<WeightedWord> wwords = StopwordsFilter.filter(new TFIDFCalculator().getScores(lemmasArray));

            // list sorting
            Collections.sort(wwords, new Comparator<WeightedWord>() {
                public int compare(WeightedWord w1, WeightedWord w2) {
                    return (-1) * new Double(w1.getScore()).compareTo(new Double(w2.getScore()));
                }
            });

            keywords = wordsToList(wwords);
            this.sender.send(ZMQTOPIC_FBKEYWORDS, keywords);
            this.sender.send(ZMQTOPIC_FBPICTURE,  fbConnector.getUserprofilePicture());

            this.emitter = new ZMQEmitter(5565, ZMQTOPIC_FBDEFAULT);

            emitter.set(ZMQTOPIC_FBLIKES, fbConnector.getPostLiked());
            emitter.set(ZMQTOPIC_FBPICTURE, fbConnector.getUserprofilePicture());
            emitter.set(ZMQTOPIC_FBKEYWORDS, keywords);
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("exception caught while setting emitter default values");
        }
    }

    public void update(Observable obs, Object arg) {
        System.out.println("RECEIVED DATA");
        System.out.println(arg);

        if(obs == zmqrFbDefault) {
            Response r = fbConnector.get(arg.toString());
            System.out.println("Sent it, and received:");
            System.out.println(r.getCode());
            System.out.println(r.getBody());
            sender.send(ZMQTOPIC_FBDEFAULT, r.getBody());
            sender.send("fb/" + arg.toString(), r.getBody());
        }

        if(obs == zmqrFbKeywords) {
            System.out.println("Trying to TFIDF your liked posts...");
            String[] data = (fbConnector.getLikedPosts());
            System.out.println("TFIDFing...");
            List<String> rawWords = FacebookPostTokenizer.tokenize(data);
            List<String> lemmas = NounVerbFilter.filter(rawWords);
            String[] lemmasArray = lemmas.toArray(new String[0]);
            List<WeightedWord> wwords = StopwordsFilter.filter(new TFIDFCalculator().getScores(lemmasArray));

            // list sorting
            Collections.sort(wwords, new Comparator<WeightedWord>() {
                public int compare(WeightedWord w1, WeightedWord w2) {
                    return (-1) * new Double(w1.getScore()).compareTo(new Double(w2.getScore()));
                }
            });

            System.out.println("Done. Here's a top 10");
            String keywords = wordsToList(wwords);

            sender.send(ZMQTOPIC_FBDEFAULT, keywords);
            sender.send(ZMQTOPIC_FBKEYWORDS, keywords);
            emitter.set(ZMQTOPIC_FBKEYWORDS, keywords);
        }

        if(obs == zmqrFbLikes) {
            String response = fbConnector.getPostLiked();
            System.out.println("Sent it, and received:");
            System.out.println(response);
            sender.send(ZMQTOPIC_FBDEFAULT, response);
            sender.send(ZMQTOPIC_FBLIKES, response);
            emitter.set(ZMQTOPIC_FBLIKES, response);
        }

        if(obs == zmqrFbPicture) {
            String response = fbConnector.getUserprofilePicture();
            System.out.println("Sent it, and received:");
            System.out.println(response);
            sender.send(ZMQTOPIC_FBDEFAULT, response);
            sender.send(ZMQTOPIC_FBPICTURE, response);
            emitter.set(ZMQTOPIC_FBPICTURE, response);
        }
    }

    private FacebookConnector fbConnector;
    private ZMQSender sender;
    private ZMQEmitter emitter;

    private ZMQReceiver zmqrFbLikes,
                        zmqrFbPicture,
                        zmqrFbKeywords,
                        zmqrFbDefault;

    public static final String ZMQTOPIC_FBLIKES = "fb/likes",
                               ZMQTOPIC_FBPICTURE = "fb/picture",
                               ZMQTOPIC_FBKEYWORDS = "fb/keywords",
                               ZMQTOPIC_FBDEFAULT = "fb/data";
                               

    public static void main(String[] args) {
       new Application().themain();
    }

    public void themain() {
        try {    

            System.setProperty("treetagger.home", System.getProperty("user.dir") + "/treetagger");
    
            System.out.println("TreeHugger configurés: home = " + System.getProperty("treetagger.home"));
    
            System.out.println("Setting Facebook Model...");
            fbConnector = new FacebookConnector();
            Scanner in = new Scanner(System.in);
            this.sender = new ZMQSender();

            // Obtain the Authorization URL
            System.out.println("Fetching the Authorization URL...");
            String authorizationUrl = fbConnector.getAuthorizationUrl();
            System.out.println("Récupération de l'URL d'authorisation effectuée!");
            System.out.println("Maintenant accepte l'application, et copie le code :");
            System.out.println(authorizationUrl);
            System.out.println("Copie maintenant le code d'authentification ici");
            System.out.print(">>");
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            desktop.browse(new URI(authorizationUrl));
            String code = in.nextLine();
            this.doTraitement(code);
            this.setObserver();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception caught");
        }
    }

    private String wordsToList(List<WeightedWord> wwords) {
        int nbLeft = 50;
        String keywords = "";
        String prefix = "";
        for(WeightedWord word : wwords) {
            keywords += prefix + word.getWord();
            prefix = ";";
            System.out.println("" + (51 - nbLeft) + " : " + word.getWord() + " - " + word.getScore() * wwords.size());
            nbLeft --;
            if(nbLeft <= 0) break;
        }
        return keywords;
    }
}
