package subscribers;

import config.subconfig.Config;
import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class EmotionSubscriber extends ThreadedSubscriber {

    public EmotionSubscriber(Config aConfig, ZMQ.Context context) {super(aConfig, context);}

    public void run() {
        while (true){
            System.out.println("topic: "+this.topic);
            String emotion = subscriber.recvStr().trim();
            System.out.println(emotion);
            oscsender.sendOscMessage(config,emotion);
        }
    }
}
