package fr.insa.ihme.emotion.modules;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import fr.insa.ihme.emotion.publisher.PublisherEmotion;

public class MoodPatrolEmotion extends AbstractAPIExecutor {
    private String topicSentiment;
    private String topicSentiment2; // Not used
    private String mashapeKey;
    private PublisherEmotion publisher;


    public MoodPatrolEmotion(String mashapeKey, PublisherEmotion publisher, String pubToSentiment, String pubToSentiment2) {
        this.mashapeKey = mashapeKey;
        this.publisher = publisher;
        this.topicSentiment = pubToSentiment;
        this.topicSentiment2 = pubToSentiment2;
    }


    public void run() {
        String result;
        String split[];

        while (true) {
            result = subscriber.recvStr().trim();
            //split pour enlever topic (topic::content)
            split = result.split("::");

            try {
                HttpResponse<JsonNode> response = Unirest.post("https://shl-mp.p.mashape.com/webresources/jammin/emotionV2")
                        .header("X-Mashape-Key", mashapeKey)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .field("lang", "en")
                        .field("text", split[1])
                        .asJson();

                // TODO check status OK
                // System.out.println(response.getStatus());

                String sentiment = response.getBody().getObject().getJSONArray("groups").getJSONObject(0).getString("name");
                
                
                // Sentiment 2 not used
                //String sentiment2 = response.getBody().getObject().getJSONArray("groups").getJSONObject(1).getString("name");

                publisher.publish(topicSentiment, sentiment);
                //publisher.publish(topicSentiment2, sentiment2);

            } catch (UnirestException e) {
                subscriber.close();
                context.term();
                e.printStackTrace();
            }
        }
    }

}
