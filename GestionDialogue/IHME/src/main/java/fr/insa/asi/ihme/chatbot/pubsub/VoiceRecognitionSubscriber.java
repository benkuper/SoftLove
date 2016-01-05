package fr.insa.asi.ihme.chatbot.pubsub;

import fr.insa.asi.ihme.chatbot.pubsub.tools.Response;

public class VoiceRecognitionSubscriber extends Subscriber{
    private Response response;

    public VoiceRecognitionSubscriber(String topic, String address, int port, Response r) {
        super(topic, address, port);
        this.response = r;
    }

    @Override
    public void run() {
        System.out.println("Run right class...");
        while (!Thread.currentThread().isInterrupted()){
            String contents = this.getSubscriber().recvStr ();

            if(contents.startsWith(this.getTopic())) {
                System.out.println("Received : " + contents);
                contents = contents.replace(this.getTopic() + " ", "");
                synchronized (response) {
                    response.setResponseString(contents);
                    response.notify();
                }
            }
        }
        this.getSubscriber().close ();
        this.getContext().term ();
    }

}
