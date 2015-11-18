package fr.insarouen.asi.ihme.tweetanalysis;

import fr.insarouen.asi.ihme.tweetanalysis.fb.*;
import fr.insarouen.asi.ihme.tweetanalysis.tfidf.*;
import fr.insarouen.asi.ihme.tweetanalysis.zmq.*;

import java.util.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

public class Application implements Observer {

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
            List<WeightedWord> wwords = StopwordsFilter.filter(new TFIDFCalculator().getScores(data));

            // list sorting
            Collections.sort(wwords, new Comparator<WeightedWord>() {
                public int compare(WeightedWord w1, WeightedWord w2) {
                    return  new Double(w1.getScore()).compareTo(new Double(w2.getScore()));
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
            // System.setProperty("http.proxyHost", "cachemad.insa-rouen.fr");
            // System.setProperty("http.proxyPort", "3128");
    
            // System.setProperty("https.proxyHost", "cachemad.insa-rouen.fr");
            // System.setProperty("https.proxyPort", "3128");
    
            // System.out.println("Proxy set");
    
            System.out.println("Setting Facebook Model...");
            fbConnector = new FacebookConnector();
            Scanner in = new Scanner(System.in);
            this.sender = new ZMQSender();

            // Obtain the Authorization URL
            System.out.println("Fetching the Authorization URL...");
            String authorizationUrl = fbConnector.getAuthorizationUrl();
            System.out.println("Got the Authorization URL!");
            System.out.println("Now go and authorize here:");
            System.out.println(authorizationUrl);
            System.out.println("And paste the authorization code here");
            System.out.print(">>");
            fbConnector.connect(in.nextLine());
            System.out.println();
            try {
                System.out.println(fbConnector.getPostLiked());
                System.out.println(fbConnector.getUserprofilePicture());
            } catch(Exception ex) {
                System.out.println("Well this didn't work for some reason. Maybe there's nothing to get.");
            }

            System.out.println();
            String keywords = "";
            try {
                System.out.println("Trying to TFIDF your liked posts...");
                String[] data = (fbConnector.getLikedPosts());
                System.out.println("TFIDFing...");
                List<WeightedWord> wwords = StopwordsFilter.filter(new TFIDFCalculator().getScores(data));

                // list sorting
                Collections.sort(wwords, new Comparator<WeightedWord>() {
                    public int compare(WeightedWord w1, WeightedWord w2) {
                        return  new Double(w1.getScore()).compareTo(new Double(w2.getScore()));
                    }
                });

                System.out.println("Done. Here's a top 10");
                keywords = wordsToList(wwords);
                for(WeightedWord word : wwords) {
                    this.sender.send("fb/picture",fbConnector.getUserprofilePicture());
                }

            } catch(Exception ex) {
                System.out.println("Well this didn't work for some reason. Maybe there's nothing to get.");
            }


            zmqrFbLikes = new ZMQReceiver(5563, ZMQTOPIC_FBLIKES);
            zmqrFbPicture = new ZMQReceiver(5563, ZMQTOPIC_FBPICTURE);
            zmqrFbKeywords = new ZMQReceiver(5563, ZMQTOPIC_FBKEYWORDS);
            zmqrFbDefault = new ZMQReceiver(5563, ZMQTOPIC_FBDEFAULT);

            zmqrFbLikes.addObserver(this);
            zmqrFbPicture.addObserver(this);
            zmqrFbKeywords.addObserver(this);
            zmqrFbDefault.addObserver(this);

            this.emitter = new ZMQEmitter(5565, ZMQTOPIC_FBDEFAULT);

            try {
                emitter.set(ZMQTOPIC_FBLIKES, fbConnector.getPostLiked());
                emitter.set(ZMQTOPIC_FBPICTURE, fbConnector.getUserprofilePicture());
                emitter.set(ZMQTOPIC_FBKEYWORDS, keywords);
            } catch(Exception ex) {
                ex.printStackTrace();
                System.out.println("exception caught while setting emitter default values");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("exception caught ;_;");
        }
    }

    private String wordsToList(List<WeightedWord> wwords) {

                int nbLeft = 10;
                String keywords = "";
                String prefix = "";
                for(WeightedWord word : wwords) {
                    keywords += prefix + word.getWord();
                    prefix = ";";
                    System.out.println("" + (11 - nbLeft) + " : " + word.getWord() + " - " + word.getScore() * wwords.size());
                    nbLeft --;
                    if(nbLeft <= 0) break;
                }
                return keywords;
    }
}