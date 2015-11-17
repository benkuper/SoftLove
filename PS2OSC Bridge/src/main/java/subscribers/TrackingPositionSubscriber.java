package subscribers;

import config.subconfig.Config;
import org.zeromq.ZMQ;

/**
 * Created by pbesson on 02/11/15.
 */
public class TrackingPositionSubscriber extends ThreadedSubscriber {

    public TrackingPositionSubscriber(Config aConfig, ZMQ.Context context) {super(aConfig, context);}

    public void run() {
        while (true){
            System.out.println("topic: "+this.topic);
            String data = subscriber.recvStr().trim();
            String delims = "[:]";
            String[] tokens = data.split(delims);
            for(String token: tokens) System.out.println(token);
        }
    }
}
