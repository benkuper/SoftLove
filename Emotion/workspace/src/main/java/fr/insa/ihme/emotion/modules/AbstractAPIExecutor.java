package fr.insa.ihme.emotion.modules;

import org.zeromq.ZMQ;

/**
 * Created by etienne on 26/10/15.
 */
public abstract class AbstractAPIExecutor implements Runnable {

    protected ZMQ.Context context;
    protected ZMQ.Socket subscriber;

    public void connectAndSubscribe(String addr, String topic, int ioThreads) {
    	System.out.println("connect to" + addr + topic);
    	
        context = ZMQ.context(ioThreads);
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(addr);

        subscriber.subscribe(topic.getBytes());

    }

    public void close() {
        subscriber.close();
        context.term();
    }
}
