package fr.insarouen.asi.ihme.tweetanalysis.zmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class ZMQSender {
    private Context context;
    private Socket publisher;

    public ZMQSender() {
        this.connect("tcp://localhost:5563");
    }

    public ZMQSender(int port) {
        this.connect("tcp://localhost:" + port);
    }

    public ZMQSender(String bindTo) {
        this.connect(bindTo);
    }

    public void connect(String bindTo) {
        this.context = ZMQ.context(1);
        this.publisher = context.socket(ZMQ.PUB);
        this.publisher.bind(bindTo); // "tcp://localhost:5563"
    }

    public void close() {
        this.publisher.close();
        this.context.term();
    }

    public void send(String header, String content) {
        this.publisher.sendMore(header);
        this.publisher.send(content);
    }

}
