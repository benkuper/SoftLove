package src;

import org.zeromq.*;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class ZMQConnector {

	private static String IP_PUBLISHER = "localhost";//"172.18.23.217";
	
	public static Socket keywords_publisher;
	public static Socket images_publisher;

	private static String IP_KEYWORDS_SUBSCRIBER = "localhost";//"172.18.19.27";
	private static String IP_SOCIALNETWORK_SUBSCRIBER = "localhost";//"172.18.20.101";

	public static Socket keywords_subscriber;
	public static Socket socialNetwork_subscriber;

	public ZMQConnector() {
		Context context = ZMQ.context(1);

		keywords_publisher = context.socket(ZMQ.PUB);
		images_publisher = context.socket(ZMQ.PUB);

		keywords_publisher.bind("tcp://"+IP_PUBLISHER+":6969");
		images_publisher.bind("tcp://"+IP_PUBLISHER+":6970");

		keywords_subscriber = context.socket(ZMQ.SUB);
		socialNetwork_subscriber = context.socket(ZMQ.SUB);

		keywords_subscriber.connect("tcp://"+IP_KEYWORDS_SUBSCRIBER+":5656");
		socialNetwork_subscriber.connect("tcp://"+IP_SOCIALNETWORK_SUBSCRIBER+":5563");

		keywords_subscriber.subscribe("Parole".getBytes());
		socialNetwork_subscriber.subscribe("fb/likes".getBytes());
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