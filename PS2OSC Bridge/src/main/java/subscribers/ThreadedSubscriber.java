package subscribers;

import OSCManagement.OSCManager;
import config.subconfig.Config;
import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class ThreadedSubscriber extends Thread {
    protected ZMQ.Socket subscriber;
    protected String topic;
    protected Config config;
    protected OSCManager oscsender;

    public ThreadedSubscriber(Config aConfig, ZMQ.Context context) {
        this.config = aConfig;
        this.topic = aConfig.getSourceTopic();
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(aConfig.getSourceAddress());
        subscriber.subscribe(topic.getBytes());
        this.oscsender = new OSCManager(aConfig);
    }

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
