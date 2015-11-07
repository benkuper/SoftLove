package subscribers;

import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class ThreadedSubscriber extends Thread {
    protected ZMQ.Socket subscriber;
    protected String topic;

    public ThreadedSubscriber(String topic, ZMQ.Context context, String addr) {
        this.topic = topic;
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(addr);
        subscriber.subscribe(topic.getBytes());
    }

    public void run() {
        while (true){
            System.out.println("topic: "+this.topic);
            System.out.println(subscriber.recvStr().trim());
        }
    }
}
