package fr.insa.asi.ihme.chatbot.pubsub;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.zeromq.ZMQ;

public class Publisher{
    private String address;
    private ZMQ.Context context;
    private ZMQ.Socket publisher;


    public Publisher(int context, String address){
        this.address = address;
        this.context = ZMQ.context(1);
        this.publisher = this.context.socket(ZMQ.PUB);
        this.publisher.bind(address);
    }

    public void publish(String topic, String message) {
        System.out.println("Gonna publish on : " + topic + " on : "+ this.address);
        String toSend = String.format(topic + " " + message);
        boolean isSent = this.publisher.send(toSend);
        System.out.println(toSend + " \t.....sent : " + isSent);
    }
}
