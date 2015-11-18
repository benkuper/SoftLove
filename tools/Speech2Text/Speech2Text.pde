

import muthesius.net.*;
import org.webbitserver.*;

import oscP5.*;
import netP5.*;
  
OscP5 oscP5;
NetAddress myRemoteLocation;

WebSocketP5 socket;

//ZMQ
org.zeromq.ZMQ.Context context;
org.zeromq.ZMQ.Socket publisher;


void setup() {
  socket = new WebSocketP5(this,8080);
  oscP5 = new OscP5(this,13000);
  //myRemoteLocation = new NetAddress("172.18.18.157",12000);
  myRemoteLocation = new NetAddress("127.0.0.1",12000);
  
  initZMQ();
}

void initZMQ()
{
    context = org.zeromq.ZMQ.context(1);
    publisher = context.socket(org.zeromq.ZMQ.PUB);
    // Prevent publisher overflow from slow subscribers
    publisher.setHWM(1);
    // Specify swap space in bytes, this covers all subscribers
    //publisher.setSwap(25000000);
    // We send updates via this socket
    publisher.bind("tcp://*:5656");
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
  text = sanitize(text);
  
  OscMessage myMessage = new OscMessage("/text2speech");
  myMessage.add(text); /* add an int to the osc message */
  println("send "+text);
  oscP5.send(myMessage, myRemoteLocation); 
  
  
  //ZMQ
  publisher.send("Parole",org.zeromq.ZMQ.SNDMORE);
  publisher.send(text.getBytes(),0);
}

void websocketOnOpen(WebSocketConnection con){
  println("A client joined");
  sendSpeechMsg("joined");
}

void websocketOnClosed(WebSocketConnection con){
  println("A client left");
}

String sanitize(String text)
{
  return text.replace("ç","c").replace("à","a").replace("é","e").replace("è","e");
}
