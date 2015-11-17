import config.AppConfig;
import config.ConfigManager;
import org.zeromq.ZMQ;
import subscribers.*;

/**
 * Created by pierre on 07/11/15.
 */
public class ZMQManager {

    private ZMQ.Context context;
    private AppConfig config;

    public WebImagesSubscriber rechercheWebimgs;
    public WebImagesSubscriber rechercheWebkey;
    public TrackingPositionSubscriber detectionGeste;
    public TrackingZoneSubscriber detectionZone;
    public Speech2TextSubscriber retranscriptionParole;
    public FacebookLinksSubscriber analyseTweets;
    public ChatBotSubscriber gestionDialogue;
    public EmotionSubscriber detectionEmotions;

    public ZMQManager(){

        //Importe la configuration depuis le fichier config.json
        ConfigManager cm = new ConfigManager();
        config = cm.loadConfig("config.json");

        //Initialise ZeroMQ
        context = ZMQ.context(1);

        //Initialise les threads subscribers pour chacun des topics
        rechercheWebimgs = new WebImagesSubscriber(config.getConfig("Web-images"),context);

        rechercheWebkey = new WebImagesSubscriber(config.getConfig("Web-keywords"),context);

        detectionGeste = new TrackingPositionSubscriber(config.getConfig("Tracking-geste"),context);

        detectionZone = new TrackingZoneSubscriber(config.getConfig("Tracking-zone"),context);

        retranscriptionParole = new Speech2TextSubscriber(config.getConfig("Speech2Text"),context);

        analyseTweets = new FacebookLinksSubscriber(config.getConfig("Facebook"),context);

        gestionDialogue = new ChatBotSubscriber(config.getConfig("ChatBot"),context);

        detectionEmotions = new EmotionSubscriber(config.getConfig("Emotion"),context);
    }

    public void startSubscribers(){
        //Lance les threads
        rechercheWebimgs.start();
        rechercheWebkey.start();
        detectionGeste.start();
        detectionZone.start();
        retranscriptionParole.start();
        analyseTweets.start();
        gestionDialogue.start();
        detectionEmotions.start();
    }

    public void terminateContext(){
        context.term();
    }


    public String getConfigString() {
        return config.toString();
    }

    public AppConfig getConfig(){
        return config;
    }
}
