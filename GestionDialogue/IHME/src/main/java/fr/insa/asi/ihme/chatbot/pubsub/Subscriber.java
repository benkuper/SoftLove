package fr.insa.asi.ihme.chatbot.pubsub;

import org.zeromq.ZMQ;

public class Subscriber extends Thread {
    private String topic;

    private String address;
    private int port;
    private ZMQ.Context context;

    private ZMQ.Socket subscriber;
    public Subscriber(String topic, String address, int port){
        this.setTopic(topic);
        this.address = address;
        this.port = port;
        this.setContext(ZMQ.context(1));
        this.setSubscriber(this.context.socket(ZMQ.SUB));
        this.subscriber.setIdentity(this.topic.getBytes());
        this.subscriber.connect(this.address+":"+this.port);
        this.subscriber.subscribe(this.topic.getBytes());

    }

    public ZMQ.Context getContext() {
        return context;
    }

    public void setContext(ZMQ.Context context) {
        this.context = context;
    }

    public ZMQ.Socket getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(ZMQ.Socket subscriber) {
        this.subscriber = subscriber;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public void run() {
        System.out.println("Run of super class Thread...");
    }
}
