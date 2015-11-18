ChatterBot bot1;
ChatterBot bot2;
ChatterBotSession bot1session;
ChatterBotSession bot2session;

import oscP5.*;
import netP5.*;
  
OscP5 oscP5;
NetAddress myRemoteLocation;


void setup()
{
  
  oscP5 = new OscP5(this,12000);
  myRemoteLocation = new NetAddress("172.18.18.157",12000);
  
  try
  {
    
      ChatterBotFactory factory =  new ChatterBotFactory();
     bot1 = factory.create(ChatterBotType.CLEVERBOT);
       bot1session  = bot1.createSession();

        bot2 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
       bot2session  = bot2.createSession();
  }catch(Exception e)
  {
    println("Error init factory");
  }
       

        
}

void draw()
{
  String s = "Salut";
  /*
  try{
    
    s = bot2session.think(s);
    println("bot1> " + s);
    

    s = bot1session.think(s);
    println("bot2> " + s);
  }catch(Exception e)
  {
    println("error");
  }
    */
}

void oscEvent(OscMessage msg)
{
  String txt = msg.get(0).toString();
  println("received :"+txt);

  try
  {
    String response = bot1session.think(txt);
    OscMessage m = new OscMessage("/text2speech");
    m.add(response);
    oscP5.send(m,myRemoteLocation);
    println("Send response :"+response);
  }catch(Exception e)
  {
    println("error");
  }
    
  
}
