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

	//private static String IP_KEYWORDS_SUBSCRIBER = "localhost";//"172.18.19.27";
	//private static String IP_SOCIALNETWORK_SUBSCRIBER = "localhost";//"172.18.20.101";

	public static Socket keywords_subscriber;
	public static Socket socialNetwork_subscriber;

	public ZMQConnector() throws IOException, ParseException {
		
		FileReader reader = new FileReader("config.json");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        
		Context context = ZMQ.context(1);

		keywords_publisher = context.socket(ZMQ.PUB);
		images_publisher = context.socket(ZMQ.PUB);

		keywords_publisher.bind((String) jsonObject.get("IP_KEYWORDS_PUBLISHER"));
		images_publisher.bind((String) jsonObject.get("IP_IMAGES_PUBLISHER"));

		keywords_subscriber = context.socket(ZMQ.SUB);
		socialNetwork_subscriber = context.socket(ZMQ.SUB);

		keywords_subscriber.connect((String) jsonObject.get("IP_KEYWORDS_SUBSCRIBER"));
		socialNetwork_subscriber.connect((String) jsonObject.get("IP_SOCIALNETWORK_SUBSCRIBER"));

		keywords_subscriber.subscribe(((String) jsonObject.get("KEYWORDS_SUBSCRIBER_TOPIC")).getBytes());
		socialNetwork_subscriber.subscribe(((String) jsonObject.get("SOCIALNETWORK_SUBSCRIBER_TOPIC")).getBytes());
	}

	public static void sendImage(String message) {
		System.out.println(message);
		images_publisher.sendMore("images");
		images_publisher.send(message);
	}


	public static void sendKeywords(String message) {
		keywords_publisher.sendMore("keywords");
		keywords_publisher.send(message);
	}

	public static String receiveKeywords() {
		String debug;
		// Read envelope with address
		debug = keywords_subscriber.recvStr();
		System.out.println("keywords recu " + debug);
        // Read message contents
        debug = keywords_subscriber.recvStr();
        System.out.println("keywords recu " + debug);
        return debug;
		//return keywords_subscriber.recvStr();
	}

	public static String receiveSocialNetwork() {
		String debug;
		// Read envelope with address
		debug = socialNetwork_subscriber.recvStr();
		System.out.println("socialNetwork recu " + debug);
        // Read message contents
		debug = socialNetwork_subscriber.recvStr();
		System.out.println("socialNetwork recu " + debug);
		return debug;
	}
	
	public static void main (String[] args) throws Exception {
        // Prepare our context and publisher
        int i=0;
		while(i<20) {
			ZMQConnector.sendKeywords("keywords to send !");
			i++;
			//System.out.println("send");
			Thread.sleep(3000);
        }
        System.out.println("finish");
    }
}