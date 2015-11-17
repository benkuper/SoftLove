package fr.insa.ihme.emotion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.insa.ihme.emotion.modules.*;

import org.zeromq.*;
import org.zeromq.ZMQ.Context;

import com.memetix.mst.language.Language;

import fr.insa.ihme.emotion.publisher.PublisherEmotion;
import fr.insa.ihme.emotion.publisher.PublisherEmotionImpl;

public class FinalClient {
	private final static Logger LOGGER = Logger.getLogger(FinalClient.class.getName()); 
	
	public static void main(String[] args) {
		try {
			Properties properties = new Properties();	
			InputStream is = ClassLoader.getSystemResourceAsStream("keys.properties");
			if(is == null) 
				throw new IOException("Cannot open key.properties file");
			properties.load(is);		

			final String mashapeKey = properties.getProperty("api.mashape.key");
			final String bingKey = properties.getProperty("api.bing.key");
			final String bingAppId = properties.getProperty("api.bing.id");

			Properties ipProperties = new Properties();	
			is = ClassLoader.getSystemResourceAsStream("ip.properties");
			if(is == null) 
				throw new IOException("Cannot open ip.properties file");
			ipProperties.load(is);		

			final String ipSpeechToText = ipProperties.getProperty("textToSpeech.ip");
			final String portSpeechToText = ipProperties.getProperty("textToSpeech.port");
			final String topicSpeechToText = ipProperties.getProperty("textToSpeech.topic");
			
			PublisherEmotion publisher = new PublisherEmotionImpl();
			publisher.init(1, "tcp://*:6665");


			// Create the Apis interfaces
			AbstractAPIExecutor translator = new BingTranslator(bingAppId,
					bingKey,
					Language.ENGLISH,
					publisher,
					"emotion/translator");
			System.out.println("tcp://" + ipSpeechToText + ":" + portSpeechToText + " " + topicSpeechToText);
			//translator.connectAndSubscribe("tcp://" + ipSpeechToText + ":" + portSpeechToText, topicSpeechToText, 1);
			translator.connectAndSubscribe("tcp://localhost:6666", "emotion/mockText", 1); //le seul pub en port 6666 car mock, les autres 6665
			//translator.connectAndSubscribe("tcp://172.18.19.27:5656", "Parole", 1);
			
			AbstractAPIExecutor posNeg = new PositiveNegativeAnalyse(mashapeKey,
					publisher,
					"emotion/details/posneg/sentiment",
					"emotion/details/posneg/score");
			posNeg.connectAndSubscribe("tcp://localhost:6665", "emotion/translator", 1);

			AbstractAPIExecutor mood = new MoodPatrolEmotion(mashapeKey,
					publisher,
					"emotion/details/mood/sentiment",
					"emotion/details/mood/secondsentiment");
			mood.connectAndSubscribe("tcp://localhost:6665", "emotion/translator", 1);

			AbstractAPIExecutor sentiment = new SentimentExecutor(mashapeKey,
					publisher,
					"emotion/details/sentiment/sentiment",
					"emotion/details/sentiment/confidence");
			sentiment.connectAndSubscribe("tcp://localhost:6665", "emotion/translator", 1);

			FusionMaster master = new FusionMaster(publisher, "emotion/fusion", 5);
			master.connectAndSubscribe("tcp://localhost:6665", "emotion/details/mood/sentiment",
									   "tcp://localhost:6665", "emotion/details/mood/secondsentiment",
									   1);
			
			// Launch the api executor
			new Thread(translator).start();
			//new Thread(posNeg).start();
			new Thread(mood).start();
			new Thread(sentiment).start();
			new Thread(master).start();
			
			
			// Read the topic
			ZMQ.Context context = ZMQ.context(1);

			new ThreadedSubscriber("emotion/translator", context, "tcp://localhost:6665").start();
			//new ThreadedSubscriber("emotion/details/posneg/sentiment", context, "tcp://localhost:6665").start();
			//new ThreadedSubscriber("emotion/details/posneg/score", context, "tcp://localhost:6665").start();
			//new ThreadedSubscriber("emotion/details/mood/sentiment", context, "tcp://localhost:6665").start();
			//new ThreadedSubscriber("emotion/details/mood/secondsentiment", context, "tcp://localhost:6665").start();
			//new ThreadedSubscriber("emotion/details/sentiment/sentiment", context, "tcp://localhost:6665").start();
			//new ThreadedSubscriber("emotion/details/sentiment/confidence", context, "tcp://localhost:6665").start();*/

			
			new ThreadedSubscriber("emotion/fusion/json", context, "tcp://localhost:6665").start();
			
			/*new ThreadedSubscriber("emotion/fusion/joy", context, "tcp://localhost:6665").start();
			new ThreadedSubscriber("emotion/fusion/trust", context, "tcp://localhost:6665").start();
			new ThreadedSubscriber("emotion/fusion/fear", context, "tcp://localhost:6665").start();
			new ThreadedSubscriber("emotion/fusion/sadness", context, "tcp://localhost:6665").start();
			new ThreadedSubscriber("emotion/fusion/anger", context, "tcp://localhost:6665").start();
			new ThreadedSubscriber("emotion/fusion/disgust", context, "tcp://localhost:6665").start();
			new ThreadedSubscriber("emotion/fusion/surprise", context, "tcp://localhost:6665").start();
			*/

			//new ThreadedSubscriber("GESTE", context, "tcp://172.18.18.201:5555").start();

			
			while (true) {
				// On laisse les threadSubscribers faire... :)
			}
			//subscriber.close();
			//context.term();


		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "The file keys.propeties is needed to read API keys :\napi.mashape\napi.bing");
			e.printStackTrace();
		}
	}
}

class ThreadedSubscriber extends Thread {
	private ZMQ.Socket subscriber;

	public ThreadedSubscriber(String topic, Context context, String addr) {
		subscriber = context.socket(ZMQ.SUB);
		subscriber.connect(addr);
		subscriber.subscribe(topic.getBytes());
	}

	public void run() {
		while (true)
			System.out.println(subscriber.recvStr().trim());
	}
}


