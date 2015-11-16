import config.Config;

public class Main {

  static public void main(String[] passedArgs) {

      ZMQManager manager = new ZMQManager();
      manager.startSubscribers();
      Config config = manager.getConfig();

      OSCManager osc = new OSCManager(config);
      osc.sendOscMessage("/test","test OSC !");
  }

}
