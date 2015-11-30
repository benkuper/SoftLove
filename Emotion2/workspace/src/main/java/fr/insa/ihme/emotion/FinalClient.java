package fr.insa.ihme.emotion;

import java.util.logging.Logger;

import com.memetix.mst.language.Language;

import fr.insa.ihme.emotion.modules.AbstractAPIExecutor;
import fr.insa.ihme.emotion.modules.BingTranslator;
import fr.insa.ihme.emotion.modules.FusionMaster;
import fr.insa.ihme.emotion.modules.MoodPatrolEmotion;
import fr.insa.ihme.emotion.modules.PositiveNegativeAnalyse;
import fr.insa.ihme.emotion.modules.SentimentExecutor;
import fr.insa.ihme.emotion.publisher.PublisherEmotion;
import fr.insa.ihme.emotion.publisher.PublisherEmotionImpl;

public class FinalClient {

//	private final static Logger LOGGER = Logger.getLogger(FinalClient.class.getName());

	public static void main(String[] args) {

		String mashapeKey = "";
		String bingKey = "";
		String bingAppId = "";

		if (args.length >= 3) {
			mashapeKey = args[0];
			bingKey = args[1];
			bingAppId = args[2];
			System.out.println("Using mashapeKey : \"" + mashapeKey + "\"");
			System.out.println("Using binKey : \"" + bingKey + "\"");
			System.out.println("Using bingAppId : \"" + bingAppId + "\"");
		} else {
			System.out.println("Please provide args : [0:mashapeKey], [1:bingKey],[2:bingAppId]");
		}

		final String ipSpeechToText = "127.0.0.1";
		final String portSpeechToText = "5656";
		final String topicSpeechToText = "Parole";

		PublisherEmotion publisher = new PublisherEmotionImpl();
		publisher.init(1, "tcp://*:6665");

		// Create the Apis interfaces
		AbstractAPIExecutor translator = new BingTranslator(bingAppId, bingKey, Language.ENGLISH, publisher,
				"emotion/translator");
		
		
		
		
		// Inverser les deux lignes suivante pour tester avec le vrai module ou avec le mock.
	
		// translator.connectAndSubscribe("tcp://localhost:6666", "emotion/mockText", 1); // le seul pub en port 6666 car mock, les autres 6665
		
		translator.connectAndSubscribe("tcp://" + ipSpeechToText + ":" + portSpeechToText, topicSpeechToText, 1);
		
		
		
		
		
		AbstractAPIExecutor posNeg = new PositiveNegativeAnalyse(mashapeKey, publisher,
				"emotion/details/posneg/sentiment", "emotion/details/posneg/score");
		posNeg.connectAndSubscribe("tcp://localhost:6665", "emotion/translator", 1);

		AbstractAPIExecutor mood = new MoodPatrolEmotion(mashapeKey, publisher, "emotion/details/mood/sentiment",
				"emotion/details/mood/secondsentiment");
		mood.connectAndSubscribe("tcp://localhost:6665", "emotion/translator", 1);

		AbstractAPIExecutor sentiment = new SentimentExecutor(mashapeKey, publisher,
				"emotion/details/sentiment/sentiment", "emotion/details/sentiment/confidence");
		sentiment.connectAndSubscribe("tcp://localhost:6665", "emotion/translator", 1);

		FusionMaster master = new FusionMaster(publisher, "emotion/fusion", 5);
		master.connectAndSubscribe("tcp://localhost:6665", "emotion/details/mood/sentiment", "tcp://localhost:6665",
				"emotion/details/mood/secondsentiment", "tcp://localhost:6667", "band/heartbeat",
				"tcp://localhost:6665", "emotion/details/sentiment/sentiment", "tcp://localhost:6665",
				"emotion/details/sentiment/confidence", 1);

		// Launch the api executor
		new Thread(translator).start();

		// new Thread(posNeg).start(); // seems broken API

		new Thread(mood).start();
		new Thread(sentiment).start();
		new Thread(master).start();

		// CAN BE USED FOR DEBUG, JUST DISPLAYING CONTENT OF TOPIC
		/*
		 * new ThreadedSubscriber("emotion/translator", context,
		 * "tcp://localhost:6665").start(); new
		 * ThreadedSubscriber("emotion/details/posneg/sentiment", context,
		 * "tcp://localhost:6665").start(); new
		 * ThreadedSubscriber("emotion/details/posneg/score", context,
		 * "tcp://localhost:6665").start(); new
		 * ThreadedSubscriber("emotion/fusion/json", context,
		 * "tcp://localhost:6665").start(); new
		 * ThreadedSubscriber("band/heartbeat", context,
		 * "tcp://localhost:6667").start();
		 */

		while (true) {
			// On laisse les threadSubscribers faire... :)
		}

	}
}

// USE FOR DEBUG ONLY
/*
 * class ThreadedSubscriber extends Thread { private ZMQ.Socket subscriber;
 * 
 * public ThreadedSubscriber(String topic, Context context, String addr) {
 * subscriber = context.socket(ZMQ.SUB); subscriber.connect(addr);
 * subscriber.subscribe(topic.getBytes()); }
 * 
 * public void run() { while (true)
 * System.out.println(subscriber.recvStr().trim()); } }
 */
