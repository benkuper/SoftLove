package fr.insarouen.asi.ihme.tweetanalysis;

import fr.insarouen.asi.ihme.tweetanalysis.zmq.*;
import java.util.*;

public class ZMQTest implements Observer {

    public static void main(String[] args) {
        new ZMQTest();
        try {
            Thread.sleep(5000);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public ZMQTest() {
        try {
            System.out.println("GUTEN TAG WELD!");

            ZMQSender s = new ZMQSender();
            ZMQReceiver r = new ZMQReceiver(5563, "B");
            r.addObserver(this);

            try {
                Thread.sleep(300);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            s.send("B", "sending data");
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("exception caught ;_;");
        }
    }

    public void update(Observable wat, Object waaat) {
        System.out.println("Message has been sent");
        System.out.println(wat);
        System.out.println(waaat);
    }
}
