package fr.insa.ihme.emotion.modules;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fr.insa.ihme.emotion.publisher.PublisherEmotion;
import org.json.JSONObject;

/**
 * Created by etienne on 26/10/15.
 */
public class SentimentExecutor extends AbstractAPIExecutor {

    // TODO create an MashapeAPIExecutor to handle those field and the constructor
    private String mashapeKey;
    private String pubToSentimentText;
    private String pubToSentimentConfidence;
    private PublisherEmotion publisher;

    public SentimentExecutor(String key, PublisherEmotion publisher, String pubToSentimentTexte, String pubToSentimentConfidence) {
        this.mashapeKey = key;
        this.publisher = publisher;
        this.pubToSentimentText = pubToSentimentTexte;
        this.pubToSentimentConfidence = pubToSentimentConfidence;
    }

    public void run() {
        String result;
        String split[];

        while (true) {
            result = subscriber.recvStr().trim();
            //split pour enlever topic (topic::content)
            split = result.split("::");
            try {
                HttpResponse<JsonNode> response = Unirest.post("https://community-sentiment.p.mashape.com/text/")
                        .header("X-Mashape-Key", mashapeKey)
                        .header("Accept", "application/json")
                        .field("txt", result)
                        .asJson();

                JSONObject sentimentResults = response.getBody().getObject().getJSONObject("result");
                String sentimentText = sentimentResults.get("sentiment").toString();
                String sentimentScore = sentimentResults.get("confidence").toString();


                publisher.publish(pubToSentimentText, sentimentText);
                publisher.publish(pubToSentimentConfidence, sentimentScore);

            } catch (UnirestException e) {
                subscriber.close();
                context.term();
                e.printStackTrace();
            }
        }
    }
}
