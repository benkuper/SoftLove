import OSCManagement.OSCManager;
import config.AppConfig;

public class Main {

  static public void main(String[] passedArgs) {

    if(OSCManager.getInstance()==null) System.out.println("error in OSCManager instance");

    else System.out.println("OSCManager ready");

  }

}
