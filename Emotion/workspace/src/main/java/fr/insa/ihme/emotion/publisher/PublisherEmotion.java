package fr.insa.ihme.emotion.publisher;

public interface PublisherEmotion {
	public void init(int ioThreads, String addr);
	public void publish(String topic, String content);
	public void close();
}
