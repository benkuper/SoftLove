package src;

import java.io.FileReader;
import java.io.IOException;

import org.zeromq.*;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ZMQConnector {

	//private static String IP_PUBLISHER = "localhost";//"172.18.23.217";
	
	public static Socket keywords_publisher;
	public static Socket images_publisher;
	public static Socket amazon_publisher;

	//private static String IP_KEYWORDS_SUBSCRIBER = "localhost";//"172.18.19.27";
	//private static String IP_SOCIALNETWORK_SUBSCRIBER = "localhost";//"172.18.20.101";

	public static Socket keywords_subscriber;
	public static Socket socialNetwork_subscriber;

	public ZMQConnector() throws IOException, ParseException {
		
		FileReader reader = new FileReader("config.json");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

		System.out.println("@ keywordsPub : "+(String) jsonObject.get("IP_KEYWORDS_PUBLISHER"));
		System.out.println("@ imagesPub : "+(String) jsonObject.get("IP_IMAGES_PUBLISHER"));
		System.out.println("@ amazonPub : "+(String) jsonObject.get("IP_AMAZON_PUBLISHER"));
		System.out.println("keywordsSub : "+(String) jsonObject.get("IP_KEYWORDS_SUBSCRIBER")+" ; topic : "+((String) jsonObject.get("KEYWORDS_SUBSCRIBER_TOPIC")));
		System.out.println("socialSub : "+(String) jsonObject.get("IP_SOCIALNETWORK_SUBSCRIBER")+" ; topic : "+((String) jsonObject.get("SOCIALNETWORK_SUBSCRIBER_TOPIC")));
        
		Context context = ZMQ.context(1);

		keywords_publisher = context.socket(ZMQ.PUB);
		images_publisher = context.socket(ZMQ.PUB);
		amazon_publisher = context.socket(ZMQ.PUB);

		keywords_publisher.bind((String) jsonObject.get("IP_KEYWORDS_PUBLISHER"));
		images_publisher.bind((String) jsonObject.get("IP_IMAGES_PUBLISHER"));
		amazon_publisher.bind((String) jsonObject.get("IP_AMAZON_PUBLISHER"));

		keywords_subscriber = context.socket(ZMQ.SUB);
		socialNetwork_subscriber = context.socket(ZMQ.SUB);

		keywords_subscriber.connect((String) jsonObject.get("IP_KEYWORDS_SUBSCRIBER"));
		socialNetwork_subscriber.connect((String) jsonObject.get("IP_SOCIALNETWORK_SUBSCRIBER"));

		keywords_subscriber.subscribe(((String) jsonObject.get("KEYWORDS_SUBSCRIBER_TOPIC")).getBytes());
		socialNetwork_subscriber.subscribe(((String) jsonObject.get("SOCIALNETWORK_SUBSCRIBER_TOPIC")).getBytes());
	}

	public static void sendImage(String message) {
		images_publisher.sendMore("images");
		images_publisher.send(message);
		System.out.println("image envoyé : " + message);
	}


	public static void sendKeywords(String message) {
		keywords_publisher.sendMore("keywords");
		keywords_publisher.send(message);
		System.out.println("keywords envoyé : " + message);
	}

	public static String receiveKeywords() {
		String debug;
		// Read envelope with address
		debug = keywords_subscriber.recvStr();
		System.out.println("keywords recu : " + debug);
        // Read message contents
        debug = keywords_subscriber.recvStr();
        System.out.println("keywords recu : " + debug);
        return debug;
		//return keywords_subscriber.recvStr();
	}

	public static String receiveSocialNetwork() {
		String debug;
		// Read envelope with address
		debug = socialNetwork_subscriber.recvStr();
		System.out.println("socialNetwork recu : " + debug);
        // Read message contents
		debug = socialNetwork_subscriber.recvStr();
		System.out.println("socialNetwork recu : " + debug);
		return debug;
	}
	

	public static void sendAmazon(String message) {
		amazon_publisher.sendMore("amazon");
		amazon_publisher.send(message);
		System.out.println("amazon envoyé : " + message);
		
	}
}