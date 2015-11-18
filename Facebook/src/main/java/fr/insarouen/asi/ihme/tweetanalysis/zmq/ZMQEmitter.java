package fr.insarouen.asi.ihme.tweetanalysis.zmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.json.*;

import java.util.*;

public class ZMQEmitter extends ZMQSender {

    JSONObject obj = new JSONObject();
    String topic = "";

    protected class EmitterTask extends TimerTask {
        public void run() {
            send(topic, obj.toString());
        }
    }

    private EmitterTask emitterTask;
    private Timer emitterTimer;

    public ZMQEmitter(int port, String topic) {
        super(port);
        this.topic = topic;
    }

    public ZMQEmitter(String bindTo, String topic) {
        super(bindTo);
        this.topic = topic;
    }

    public void connect(String bindTo) {
        super.connect(bindTo);
        emitterTask = new EmitterTask();
        emitterTimer = new Timer();
        emitterTimer.schedule(emitterTask, 0, 1000);
    }

    public void close() {
        super.close();
        // TODO close emitterTimer
    }

    public void set(String header, Object data) {
        obj.put(header, data);
    }

}
