package fr.insarouen.asi.ihme.tweetanalysis.zmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.Observable;

public class ZMQReceiver extends Observable {

    public class IncomingMessageWatcher extends Thread {

        private ZMQReceiver parent;
        private volatile boolean watching = true;

        public IncomingMessageWatcher(ZMQReceiver parent) {
            this.parent = parent;
            this.start();
        }

        public void run() {
            
            while (watching) {
                String contents = this.parent.subscriber.recvStr ();
                this.parent.onReceive(contents);
            }
        }

        public void nicelyStop() {
            this.watching = false;
        }        
    }


    private Context context = ZMQ.context(1);
    protected Socket subscriber = context.socket(ZMQ.SUB);
    private String subscribeTo = "";
    private IncomingMessageWatcher imw;

    public ZMQReceiver(String subscribeTo) {
        this.connect(5563);
        this.subscribeTo = subscribeTo;
    }

    public ZMQReceiver(int port, String subscribeTo) {
        this.connect(port);
        this.subscribeTo = subscribeTo;
    }

    public void connect(int port) {
        this.subscriber.connect("tcp://localhost:" + port);
        this.subscriber.subscribe(this.subscribeTo.getBytes(ZMQ.CHARSET));
        this.imw = new IncomingMessageWatcher(this);
    }

    public void close() {
        this.subscriber.close();
        this.context.term();
    }

    protected void onReceive(String msg) {
        this.setChanged();
        this.notifyObservers(msg);
    }

}