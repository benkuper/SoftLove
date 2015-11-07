
void setup() {
  ZMQManager zmq = new ZMQManager();
  System.out.print(zmq.getConfig());  
  size(640, 360);
  
}

void draw() {
  background(50);
  textSize(32);
  text("word",100,100); 
}