/*
  Simple WebSocketServer example that can receive voice transcripts from Chrome
 */

import muthesius.net.*;
import org.webbitserver.*;

import oscP5.*;
import netP5.*;
  
OscP5 oscP5;
NetAddress myRemoteLocation;

WebSocketP5 socket;

void setup() {
  socket = new WebSocketP5(this,8080);
  oscP5 = new OscP5(this,13000);
  //myRemoteLocation = new NetAddress("172.18.18.157",12000);
  myRemoteLocation = new NetAddress("127.0.0.1",12000);
}

void draw() {}

void stop(){
  socket.stop();
}

void websocketOnMessage(WebSocketConnection con, String msg){
  println(msg);
  sendSpeechMsg(msg);
  if (msg.contains("hello")) println("Yay!");
}

void sendSpeechMsg(String text)
{
  OscMessage myMessage = new OscMessage("/text2speech");
  myMessage.add(text); /* add an int to the osc message */
  println("send");
  oscP5.send(myMessage, myRemoteLocation); 
}

void websocketOnOpen(WebSocketConnection con){
  println("A client joined");
  sendSpeechMsg("joined");
}

void websocketOnClosed(WebSocketConnection con){
  println("A client left");
}
