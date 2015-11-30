package fr.insa.ihme.emotion.modules;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import fr.insa.ihme.emotion.publisher.PublisherEmotion;

public class BingTranslator extends AbstractAPIExecutor {
    private String topic;
    private Language targetLanguage;
    private PublisherEmotion publisher;

    public BingTranslator(String clientId, String key, Language targetLanguage, PublisherEmotion publisher, String pubTo) {
        Translate.setClientId(clientId);
        Translate.setClientSecret(key);
        this.targetLanguage = targetLanguage;
        this.publisher = publisher;
        this.topic = pubTo;
    }

    public void run() {
        String result;
        String split[];
        String translatedString = null;
        while (true) {
            result = subscriber.recvStr().trim();
            split = result.split("::"); //split pour enlever topic (topic::content)

            try {
            	
            	
            	// TODO : inverser la ligne commentée selon si utilisation local avec PublishTextMock ou utilisation sur réseau 
                
            	//translatedString = Translate.execute(result, targetLanguage); // pour utilisation avec module "Parole"
            	
            	translatedString = Translate.execute(split[1], targetLanguage); //pour utilisation local
            	
            	
            	
            	publisher.publish(topic, translatedString);
            } catch (Exception e) {
                close();
                e.printStackTrace();
            }
        }
    }
}
