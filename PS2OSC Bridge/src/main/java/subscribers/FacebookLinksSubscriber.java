package subscribers;

import config.subconfig.Config;
import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class FacebookLinksSubscriber extends ThreadedSubscriber {

    public FacebookLinksSubscriber(Config aConfig, ZMQ.Context context) {super(aConfig, context);}

    public void run() {
        while (true){
            System.out.println("topic: "+this.topic);
            String links = subscriber.recvStr().trim();
            System.out.println(links);
            oscsender.sendOscMessage(config,links);
        }
    }
}
