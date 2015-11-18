package fr.insa.ihme.emotion.mock;

import java.util.Scanner;

import org.zeromq.ZMQ;

public class PublishTextMock {
	public static void main(String[] args) {
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket publisher = context.socket(ZMQ.PUB);
		
		publisher.bind("tcp://*:6666");
		
		Scanner sc = new Scanner(System.in);
		
		while(true){
			publisher.send("emotion/mockText" + "::" + sc.nextLine());
		}
		
		//publisher.close();
		//context.term();
	}
}
