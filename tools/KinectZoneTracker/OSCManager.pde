public class OSCManager
{
  OscP5 oscP5;
  
  NetAddress[] remotes;
  
  public OSCManager(PApplet parent)
  {
    
    oscP5 = new OscP5(parent,9099);
    
    String[] oscList = loadStrings("osc.txt");
    remotes = new NetAddress[oscList.length];
    int index = 0;
    for(String ol : oscList) 
    {
      String[] olSplit = ol.split(":");
      remotes[index] = new NetAddress(olSplit[0],parseInt(olSplit[1]));
      index++;
    }
  }
  
  public void sendZoneChange(Zone z)
  {
    OscMessage msg = new OscMessage( "/tracking/zone");
    msg.add(z.name);
    msg.add(z.isActive);
    sendToRemotes(msg);
    
    msg = new OscMessage("/tracking/mainZone");
    msg.add(z.name);
    sendToRemotes(msg);
  }
  
  public void sendZonePointMoved(Zone z)
  { 
    OscMessage msg = new OscMessage( "/tracking/averagePos");
    msg.add(z.name);
    msg.add(z.averageZonePointNormalized.x);
    msg.add(z.averageZonePointNormalized.y);
    msg.add(z.averageZonePointNormalized.z);
    sendToRemotes(msg); 
  }
  
  
   void sendToRemotes(OscMessage msg)
  {
    for(NetAddress na: remotes)
    {
     oscP5.send(msg,na);
    } 
  }
}
