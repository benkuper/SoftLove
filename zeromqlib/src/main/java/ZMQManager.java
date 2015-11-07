import config.Config;
import config.ConfigManager;
import org.zeromq.ZMQ;
import subscribers.*;

/**
 * Created by pierre on 07/11/15.
 */
public class ZMQManager {

    private ZMQ.Context context;
    private Config config;

    public RechercheWebSubscriber rechercheWeb;
    public DetectionGesteSubscriber detectionGeste;
    public RetranscriptionParoleSubscriber retranscriptionParole;
    public AnalyseTweetsSubscriber analyseTweets;
    public GestionDialogueSubscriber gestionDialogue;
    public DetectionEmotionsSubscriber detectionEmotions;

    public ZMQManager(){

        //Importe la configuration depuis le fichier config.json
        ConfigManager cm = new ConfigManager();
        config = cm.loadConfig("config.json");

        //Initialise ZeroMQ
        context = ZMQ.context(1);

        //Initialise les threads subscribers pour chacun des topics
        System.out.println("Recherche Web : "+config.RechercheWebAdresse+" / "+config.RechercheWebTopic);
        rechercheWeb = new RechercheWebSubscriber(config.RechercheWebTopic, context,config.RechercheWebAdresse);

        System.out.println("DetectionGeste : "+config.DetectionGesteAdresse+" / "+config.DetectionGesteTopic);
        detectionGeste = new DetectionGesteSubscriber(config.DetectionGesteTopic, context,config.DetectionGesteAdresse);

        System.out.println("RetranscriptionParole : "+config.RetranscriptionParoleAdresse+" / "+config.RetranscriptionParoleTopic);
        retranscriptionParole = new RetranscriptionParoleSubscriber(config.RetranscriptionParoleTopic, context,config.RetranscriptionParoleAdresse);

        System.out.println("AnalyseTweets : "+config.AnalyseTweetsAdresse+" / "+config.AnalyseTweetsTopic);
        analyseTweets = new AnalyseTweetsSubscriber(config.AnalyseTweetsTopic, context,config.AnalyseTweetsAdresse);

        System.out.println("GestionDialogue : "+config.GestionDialogueAdresse+" / "+config.GestionDialogueTopic);
        gestionDialogue = new GestionDialogueSubscriber(config.GestionDialogueTopic, context,config.GestionDialogueAdresse);

        System.out.println("DetectionEmotions : "+config.DetectionEmotionsAdresse+" / "+config.DetectionEmotionsTopic);
        detectionEmotions = new DetectionEmotionsSubscriber(config.DetectionEmotionsTopic, context,config.DetectionEmotionsAdresse);
    }

    public void startSubscribers(){
        //Lance les threads
        rechercheWeb.start();
        //detectionGeste.start();
        //retranscriptionParole.start();
        //analyseTweets.start();
        //gestionDialogue.start();
        //detectionEmotions.start();
    }

    public void terminateContext(){
        context.term();
    }


    public String getConfigString() {
        return config.toString();
    }

    public Config getConfig(){
        return config;
    }
}
