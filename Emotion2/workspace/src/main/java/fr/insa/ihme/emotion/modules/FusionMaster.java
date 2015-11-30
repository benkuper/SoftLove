package fr.insa.ihme.emotion.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Logger;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import fr.insa.ihme.emotion.publisher.PublisherEmotion;

public class FusionMaster implements Runnable {
	private final static Logger LOGGER = Logger.getLogger(FusionMaster.class.getName()); 
	
	final int LOW_BEAT = 85;
	final int HIGH_BEAT = 110;

	final float NO_CONFIDENCE_THRESHOLD = 60; // 100 étant le plus sûr de la réponse

	private ZMQ.Context context;

	private ThreadedSubscriber mood;
	private ThreadedSubscriber mood2; // Not used
	private ThreadedSubscriber heartbeat;
	private ThreadedSubscriber sentimentPosNeg;
	private ThreadedSubscriber sentimentConfidence;

	private PublisherEmotion publisher;
	private String rootTopic;

	private HashMap<String, Float> emotionsScores = new HashMap<String, Float>();
	private CircularFifoBuffer mainMoodHistory;

	public FusionMaster(PublisherEmotion publisher, String rootTopic, int historySize) {
		this.publisher = publisher;
		this.rootTopic = rootTopic;

		this.mainMoodHistory = new CircularFifoBuffer(historySize);

		initEmotionsScores();
	}

	private void initEmotionsScores() {
		emotionsScores.clear();

		emotionsScores.put("joy", 0f);
		emotionsScores.put("trust", 0f);
		emotionsScores.put("fear", 0f);
		emotionsScores.put("sadness", 0f);
		emotionsScores.put("anger", 0f);
		emotionsScores.put("disgust", 0f);
		emotionsScores.put("surprise", 0f);
		emotionsScores.put("anticipation", 0f);

		emotionsScores.put("neutre", 0f);
	}

	public void connectAndSubscribe(String addrMood, String topicMood, String addrMood2, String topicMood2,
			String addrBand, String topicHeartbeat, String addrSentiment, String topicSentiment, 
			String addrSentimentConfidence, String topicSentimentConfidence, int ioThreads) {
		context = ZMQ.context(ioThreads);

		mood = new ThreadedSubscriber(topicMood, context, addrMood);
		heartbeat = new ThreadedSubscriber(topicHeartbeat, context, addrBand);

		mood2 = new ThreadedSubscriber(topicMood2, context, addrMood2);

		sentimentPosNeg = new ThreadedSubscriber(topicSentiment, context, addrSentiment);
		sentimentConfidence = new ThreadedSubscriber(topicSentimentConfidence, context, addrSentimentConfidence);

	}

	public void close() {
		context.term();
	}

	public void run() {

		mood.start();
		heartbeat.start();
		mood2.start();
		sentimentPosNeg.start();
		sentimentConfidence.start();

		while (true) {
			try {
				synchronized (mood) {
					String valueTopic = mood.getValueTopic();
					int intensity = computeIntensityFromHeartbeat(heartbeat.getValueTopic());

					if (valueTopic != null) {
						if(sentimentPosNeg.getValueTopic().equals("neutre") || 
								Float.parseFloat(sentimentConfidence.getValueTopic()) < NO_CONFIDENCE_THRESHOLD) {
							mainMoodHistory.add("neutre");
							intensity = 0;
						}
						else {
							mainMoodHistory.add(mood.getValueTopic());
						}
					}

					initEmotionsScores(); // Reinit to compute new score

					Iterator it = mainMoodHistory.iterator();
					int pointsToDistribute = 1;
					int total = 0;
					
					// On parcourt du plus ancien au plus récent
					// Compute a quick score with an awesome scientific proved formula! (I just deposed the copyright)
					while(it.hasNext()) {
						String key = (String) it.next();
						pointsToDistribute *= 2;
						emotionsScores.replace(key, emotionsScores.get(key) + pointsToDistribute);
						
						total += pointsToDistribute;
					}
					
					Float joy = emotionsScores.get("joy")/total*100;
					Float trust = emotionsScores.get("trust")/total*100;
					Float fear = emotionsScores.get("fear")/total*100;
					Float surprise = emotionsScores.get("surprise")/total*100;
					Float sadness = emotionsScores.get("sadness")/total*100;
					Float disgust = emotionsScores.get("disgust")/total*100;
					Float anger = emotionsScores.get("anger")/total*100;
					Float anticipation = emotionsScores.get("anticipation")/total*100;

					String json = String.format(
							Locale.US, "{\"intensity\": %d, \"moodWheel\": { \"joy\": %f, \"trust\": %f, \"fear\": %f, \"sadness\": %f, \"anger\": %f, \"disgust\": %f, \"surprise\": %f, \"anticipation\": %f}}",
							intensity, joy, trust, fear, sadness, anger, disgust, surprise, anticipation);
					

					// ---- THE FINAL PUBLISHER -----
					LOGGER.info("JSON published : " + json);
					LOGGER.info("Value of hidden NEUTRE : " + emotionsScores.get("neutre")/total*100);
					
					publisher.publish(rootTopic + "/json", json);

					// Attendre jusqu'à recevoir un nouveau contenu sur le
					// topic du thread mood.
					// Mood fait donc office d'horloge afin de ne pas
					// attendre un temps réglé arbitrairement au risque de
					// louper des infos, ou bien avoir une congestion pour
					// le sub.
					mood.wait();
				}

			} catch (InterruptedException e1) {
				e1.printStackTrace();

			}
		}
	}


	private int computeIntensityFromHeartbeat(String valueTopic) {
		if (valueTopic == null)
			return 1; // cas où le bracelet n'a rien envoyé ou pas encore envoyé

		int beat = Integer.parseInt(valueTopic);
		
		if (beat < LOW_BEAT)
			return 1;
		
		else if (beat > HIGH_BEAT)
			return 3;
		
		else return 2; // beat entre low et high
	}
}

class ThreadedSubscriber extends Thread {
	private ZMQ.Socket subscriber;
	private String valueTopic;

	public ThreadedSubscriber(String topic, Context context, String addr) {
		subscriber = context.socket(ZMQ.SUB);
		subscriber.connect(addr);
		subscriber.subscribe(topic.getBytes());
	}

	public String getValueTopic() {
		return this.valueTopic;
	}

	public void setValueTopic(String topicValue) {
		this.valueTopic = topicValue;
	}

	public void run() {
		while (true) {
			setValueTopic(subscriber.recvStr().split("::")[1]);
			synchronized (this) {
					System.out.println(valueTopic);
				this.notifyAll();
			}
		}
	}
}