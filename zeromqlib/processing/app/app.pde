
void setup() {
  ZMQManager zmq = new ZMQManager();
  System.out.print(zmq.getConfig());  
  size(640, 360);
  OSCManager osc = new OSCManager(zmq.getConfig());
  osc.sendOscMessage("test","test OSC");
  
}

void draw() {
  background(50);
  textSize(32);
  text("word",100,100); 
}