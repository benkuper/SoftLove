import config.AppConfig;

public class Main {

  static public void main(String[] passedArgs) {

      ZMQManager manager = new ZMQManager();
      manager.startSubscribers();

  }

}
