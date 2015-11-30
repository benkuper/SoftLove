package fr.insa.ihme.emotion.publisher;

import org.zeromq.ZMQ;

public class PublisherEmotionImpl implements PublisherEmotion {
	ZMQ.Context context;
	ZMQ.Socket publisher;
	
	public void init(int ioThreads, String addr) {
		context = ZMQ.context(ioThreads);
		publisher = context.socket(ZMQ.PUB);
		publisher.bind(addr);
	}

	synchronized public void publish(String topic, String content) {
		//TODO : voir sans le topic?
		publisher.send(topic + "::" + content);
	}

	public void close() {
		publisher.close();
		context.term();
	}
}
