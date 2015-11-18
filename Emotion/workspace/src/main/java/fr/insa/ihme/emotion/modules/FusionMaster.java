package fr.insa.ihme.emotion.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import fr.insa.ihme.emotion.publisher.PublisherEmotion;

public class FusionMaster implements Runnable {
	private ZMQ.Context context;
	private ZMQ.Socket subMood;
	private ZMQ.Socket subMood2;

	private PublisherEmotion publisher;
	private String rootTopic;

	private HashMap<String, Float> emotionsScores = new HashMap<String, Float>();
	private CircularFifoBuffer mainMoodHistory;
	private int historySize;

	public FusionMaster(PublisherEmotion publisher, String rootTopic, int historySize) {
		this.publisher = publisher;
		this.rootTopic = rootTopic;

		//mainMoodHistory = new ArrayBlockingQueue<String>(historySize, true);
		this.mainMoodHistory = new CircularFifoBuffer(historySize);
		this.historySize = historySize;

		initEmotionsScores();
	}

	/*
	 * Joy	Joyfulness
		Trust	Satisfaction
		Fear	Awaiting
		Sadness	Sadness
		Anger	Jealousy
		Disgust	Disgust
		Surprise	Curiosity
	 */
	private void initEmotionsScores() {
		emotionsScores.clear();

		emotionsScores.put("joy", 0f);
		emotionsScores.put("trust", 0f);
		emotionsScores.put("fear", 0f);
		emotionsScores.put("sadness", 0f);
		emotionsScores.put("anger", 0f);
		emotionsScores.put("disgust", 0f);
		emotionsScores.put("surprise", 0f);
	}

	public void connectAndSubscribe(String addrMood, String topicMood,
			String addrMood2, String topicMood2,
			int ioThreads) {
		context = ZMQ.context(ioThreads);

		subMood = context.socket(ZMQ.SUB);
		subMood.connect(addrMood);
		subMood.subscribe(topicMood.getBytes());

		subMood2 = context.socket(ZMQ.SUB);
		subMood2.connect(addrMood2);
		subMood2.subscribe(topicMood2.getBytes());
	}


	public void close() {
		subMood.close();
		subMood2.close();
		context.term();
	}

	public void run() {
		// TODO Auto-generated method stub

		ZMQ.Context context = ZMQ.context(1);

		//new ThreadedSubscriber("emotion/details/posneg/sentiment", context, "tcp://localhost:6665").start();
		//new ThreadedSubscriber("emotion/details/posneg/score", context, "tcp://localhost:6665").start();
		ThreadedSubscriber mood = new ThreadedSubscriber("emotion/details/mood/sentiment", context, "tcp://localhost:6665");
		ThreadedSubscriber mood2 = new ThreadedSubscriber("emotion/details/mood/secondsentiment", context, "tcp://localhost:6665");

		mood.start();
		mood2.start();

		while (true) {
			try {
				synchronized (mood) {
					String valueTopic = mood.getValueTopic();
					if(valueTopic != null)
						mainMoodHistory.add(mood.getValueTopic());

					if(mainMoodHistory.isFull()){
						initEmotionsScores(); // Reinit to compute new score

						Iterator it = mainMoodHistory.iterator();
						while(it.hasNext()) {
							String key = (String) it.next();

							//COMPUTE a quick score with occurrence number (TODO: some better shit right here, with %) 
							emotionsScores.replace(key, emotionsScores.get(key) + 1);
						}

						//TODO JSON
						Float joy = emotionsScores.get("joy")/historySize*100;
						Float trust = emotionsScores.get("trust")/historySize*100;
						Float fear = emotionsScores.get("fear")/historySize*100;
						Float sadness = emotionsScores.get("sadness")/historySize*100;
						Float anger = emotionsScores.get("anger")/historySize*100;
						Float disgust = emotionsScores.get("disgust")/historySize*100;
						Float surprise = emotionsScores.get("surprise")/historySize*100;
						
						String json = String.format(Locale.US, "{ \"joy\": %f, \"trust\": %f, \"fear\": %f, \"sadness\": %f, \"anger\": %f, \"disgust\": %f, \"surprise\": %f}",
								joy, trust, fear, sadness, anger, disgust, surprise);;
						System.out.println(json);
						
						publisher.publish(rootTopic+"/json", json);
						
						// Send it // Replaces by json with everything
						/*
						publisher.publish(rootTopic+"/joy", Float.toString(emotionsScores.get("joy")/historySize*100));
						publisher.publish(rootTopic+"/trust", Float.toString(emotionsScores.get("trust")/historySize*100));
						publisher.publish(rootTopic+"/fear", Float.toString(emotionsScores.get("fear")/historySize*100));
						publisher.publish(rootTopic+"/sadness", Float.toString(emotionsScores.get("sadness")/historySize*100));
						publisher.publish(rootTopic+"/anger", Float.toString(emotionsScores.get("anger")/historySize*100));
						publisher.publish(rootTopic+"/disgust", Float.toString(emotionsScores.get("disgust")/historySize*100));
						publisher.publish(rootTopic+"/surprise", Float.toString(emotionsScores.get("surprise")/historySize*100));
*/
						System.out.println("c'est publish !!!!");

						mood.wait();
					}

				}

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			/*
			 * Joy	Joyfulness
				Trust	Satisfaction
				Fear	Awaiting
				Sadness	Sadness
				Anger	Jealousy
				Disgust	Disgust
				Surprise	Curiosity
			 */
		}

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

	public String getValueTopic(){
		return this.valueTopic;
	}

	public void setValueTopic(String topicValue){
		this.valueTopic = topicValue;
	}

	public void run() {
		while (true){
			setValueTopic(subscriber.recvStr().split("::")[1]);
			synchronized (this) {
				System.out.println(valueTopic);
				this.notifyAll();				
			}
		}
	}
}
