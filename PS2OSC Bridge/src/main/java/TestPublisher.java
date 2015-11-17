import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * Created by pierre on 07/11/15.
 */
public class TestPublisher {

        public static void main (String[] args) throws Exception {
            // Prepare our context and publisher
            Context context = ZMQ.context(1);
            Socket publisher = context.socket(ZMQ.PUB);
            publisher.bind("tcp://*:5563/");
            String msg = "ceci est un simple test";
            while (!Thread.currentThread ().isInterrupted ()) {
                System.out.println(msg);
                publisher.send("rechercheweb".getBytes(),0);
                Thread.sleep(1000);
            }
            publisher.close ();
            context.term ();
        }
    }

