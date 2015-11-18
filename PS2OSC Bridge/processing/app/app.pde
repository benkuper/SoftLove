void setup() {
  ZMQManager zmqM = new ZMQManager();
  AppConfig config = zmqM.getConfig();
  
  size(640, 360);
  //OSCManager osc = new OSCManager();
  
  //osc.sendOscMessage("test","test OSC");
  
}

void draw() {
  background(50);
  textSize(32);
  text("word",100,100); 
}
