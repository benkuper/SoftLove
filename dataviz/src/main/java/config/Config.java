package config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by pierre on 07/11/15.
 */
public final class Config {

    public String RechercheWebAdresse;
    public String RechercheWebTopic;

    public String DetectionGesteAdresse;
    public String DetectionGesteTopic;

    public String RetranscriptionParoleAdresse;
    public String RetranscriptionParoleTopic;

    public String AnalyseTweetsAdresse;
    public String AnalyseTweetsTopic;

    public String GestionDialogueAdresse;
    public String GestionDialogueTopic;

    public String DetectionEmotionsAdresse;
    public String DetectionEmotionsTopic;
    public String OscRecepteurAdresse;
    public Integer OscRecepteurPort;

    public Config() {
    }

    public Config(String rechercheWebAdresse, String rechercheWebTopic, String detectionGesteAdresse, String detectionGesteTopic, String retranscriptionParoleAdresse, String retranscriptionParoleTopic, String analyseTweetsAdresse, String analyseTweetsTopic, String gestionDialoguesAdresse, String gestionDialoguesTopic, String detectionEmotionsAdresse, String detectionEmotionsTopic, String oscRecepteurAdresse, Integer oscRecepteurPort) {
        RechercheWebAdresse = rechercheWebAdresse;
        RechercheWebTopic = rechercheWebTopic;
        DetectionGesteAdresse = detectionGesteAdresse;
        DetectionGesteTopic = detectionGesteTopic;
        RetranscriptionParoleAdresse = retranscriptionParoleAdresse;
        RetranscriptionParoleTopic = retranscriptionParoleTopic;
        AnalyseTweetsAdresse = analyseTweetsAdresse;
        AnalyseTweetsTopic = analyseTweetsTopic;
        GestionDialogueAdresse = gestionDialoguesAdresse;
        GestionDialogueTopic = gestionDialoguesTopic;
        DetectionEmotionsAdresse = detectionEmotionsAdresse;
        DetectionEmotionsTopic = detectionEmotionsTopic;
        OscRecepteurAdresse = oscRecepteurAdresse;
        OscRecepteurPort = oscRecepteurPort;
    }

    @Override
    public String toString() {
        return "Config{" +
                "RechercheWebAdresse='" + RechercheWebAdresse + '\'' +
                ", RechercheWebTopic='" + RechercheWebTopic + '\'' +
                ", DetectionGesteAdresse='" + DetectionGesteAdresse + '\'' +
                ", DetectionGesteTopic='" + DetectionGesteTopic + '\'' +
                ", RetranscriptionParoleAdresse='" + RetranscriptionParoleAdresse + '\'' +
                ", RetranscriptionParoleTopic='" + RetranscriptionParoleTopic + '\'' +
                ", AnalyseTweetsAdresse='" + AnalyseTweetsAdresse + '\'' +
                ", AnalyseTweetsTopic='" + AnalyseTweetsTopic + '\'' +
                ", GestionDialogueAdresse='" + GestionDialogueAdresse + '\'' +
                ", GestionDialogueTopic='" + GestionDialogueTopic + '\'' +
                ", DetectionEmotionsAdresse='" + DetectionEmotionsAdresse + '\'' +
                ", DetectionEmotionsTopic='" + DetectionEmotionsTopic + '\'' +
                ", OscRecepteurAdresse='" + OscRecepteurAdresse + '\'' +
                ", OscRecepteurPort=" + OscRecepteurPort +
                '}';
    }

}
