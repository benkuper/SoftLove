package subscribers;

import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class AnalyseTweetsSubscriber extends ThreadedSubscriber {

    public AnalyseTweetsSubscriber(String topic, ZMQ.Context context, String addr){
        super(topic, context, addr);
    }

    public void run() {
        while (true){
            System.out.println("topic: "+this.topic);
            System.out.println(subscriber.recvStr().trim());
        }
    }
}