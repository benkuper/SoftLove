

import muthesius.net.*;
import org.webbitserver.*;
WebSocketP5 socket;

//ZMQ
org.zeromq.ZMQ.Context context;
org.zeromq.ZMQ.Socket publisher;


void setup() {
  socket = new WebSocketP5(this,8080);
  
  initZMQ();
}

void initZMQ()
{
    context = org.zeromq.ZMQ.context(1);
    publisher = context.socket(org.zeromq.ZMQ.PUB);
    // Prevent publisher overflow from slow subscribers
    publisher.setHWM(1);
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
  
  //ZMQ
  publisher.send("Parole",org.zeromq.ZMQ.SNDMORE);
  publisher.send(text.getBytes(),0);
}

void websocketOnOpen(WebSocketConnection con){
  println("A client joined");
  sendSpeechMsg("poney");
}

void websocketOnClosed(WebSocketConnection con){
  println("A client left");
}

String sanitize(String text)
{
  return text.replace("ç","c").replace("ê","e").replace("à","a").replace("é","e").replace("è","e");
}
