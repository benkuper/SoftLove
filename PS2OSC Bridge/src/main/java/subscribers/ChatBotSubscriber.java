package subscribers;

import config.subconfig.Config;
import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class ChatBotSubscriber extends ThreadedSubscriber {

    public ChatBotSubscriber(Config aConfig, ZMQ.Context context) {super(aConfig, context);}

    public void run() {
        while (true){
            System.out.println("topic: "+this.topic);
            String speech = subscriber.recvStr().trim();
            System.out.println(speech);
            oscsender.sendOscMessage(config,speech);
        }
    }
}
