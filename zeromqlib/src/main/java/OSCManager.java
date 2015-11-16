import config.Config;
import netP5.*;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscPacket;

/**
 * Created by pierre on 07/11/15.
 */
public class OSCManager {

    private Config config;
    private NetAddress receiver;

    public OSCManager(Config config) {
        this.config = config;
        //OscP5 osc = new OscP5( this , 12000 );

        System.out.println("Communication OSC  : " + config.OscRecepteurAdresse + ":" + config.OscRecepteurPort);
        this.receiver = new NetAddress(this.config.OscRecepteurAdresse, this.config.OscRecepteurPort);
    }

    public void sendOscMessage(String adressPattern, String messageContent){
        System.out.println("send osc message");
        OscMessage message = new OscMessage(adressPattern);
        message.add(messageContent);
        OscP5.flush(message, receiver);
    }
}