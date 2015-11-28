package fr.insa.ihme.emotion.modules;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import fr.insa.ihme.emotion.publisher.PublisherEmotion;

public class PositiveNegativeAnalyse extends AbstractAPIExecutor {
    private String topicSentimentTexte;
    private String topicSentimentScore;
    private String mashapeKey;
    private PublisherEmotion publisher;

    public PositiveNegativeAnalyse(String mashapeKey, PublisherEmotion publisher, String pubToSentimentTexte, String pubToSentimentScore) {
        this.mashapeKey = mashapeKey;
        this.publisher = publisher;
        this.topicSentimentTexte = pubToSentimentTexte;
        this.topicSentimentScore = pubToSentimentScore;
    }

    public void run() {
        String result;
        String split[];

        // TODO put a boolean in here
        // And refactor the 3 next lines in a parent class
        while (true) {
            result = subscriber.recvStr().trim();
            //split pour enlever topic (topic::content)
            split = result.split("::");

            try {
                HttpResponse<JsonNode> response = Unirest.get("https://loudelement-free-natural-language-processing-service.p.mashape.com/nlp-text/")
                        .header("X-Mashape-Key", mashapeKey)
                        .header("Accept", "application/json")
                        .queryString("text", split[1])
                        .asJson();

                // TODO check status OK before continue
                // TODO check get("status")
                System.out.println(response.getBody());
                String sentimentText = response.getBody().getObject().get("sentiment-text").toString();
                String sentimentScore = response.getBody().getObject().get("sentiment-score").toString();


                publisher.publish(topicSentimentTexte, sentimentText);
                publisher.publish(topicSentimentScore, sentimentScore);

            } catch (UnirestException e) {
                subscriber.close();
                context.term();
                e.printStackTrace();
            }
        }
    }

    public void close() {
        subscriber.close();
        context.term();
    }
}
