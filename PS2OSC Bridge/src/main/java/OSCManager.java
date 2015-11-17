import config.AppConfig;
import netP5.*;
import oscP5.OscMessage;
import oscP5.OscP5;

/**
 * Created by pierre on 07/11/15.
 */
public class OSCManager {

    private AppConfig config;
    private NetAddress receiver;

    public OSCManager(AppConfig config) {
        this.config = config;
        //OscP5 osc = new OscP5( this , 12000 );

        //this.receiver = new NetAddress(this.config.OscRecepteurAdresse, this.config.OscRecepteurPort);
    }

    public void sendOscMessage(String adressPattern, String messageContent){
        System.out.println("send osc message");
        OscMessage message = new OscMessage(adressPattern);
        message.add(messageContent);
        OscP5.flush(message, receiver);
    }
}