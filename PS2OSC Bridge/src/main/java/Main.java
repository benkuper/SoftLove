import config.AppConfig;

public class Main {

  static public void main(String[] passedArgs) {

      ZMQManager manager = new ZMQManager();
      manager.startSubscribers();
      AppConfig config = manager.getConfig();
/*
      OSCManager osc = new OSCManager(config);
      osc.sendOscMessage("/test","test OSC !");*/

      /*OSCConfig oscConf = new OSCConfig("127.0.0.1","12000","test");
      List<OSCConfig> listOscConf = new ArrayList<OSCConfig>();
      listOscConf.add(oscConf);

      Config conf = new Config("adress","topic",listOscConf);

      HashMap<String, Config> listConfig = new HashMap<String, Config>();
      listConfig.put("Web", conf);
      AppConfig appConfig = new AppConfig(listConfig);
      ConfigManager cm = new ConfigManager();
      cm.saveConfig(appConfig);*/
  }

}
