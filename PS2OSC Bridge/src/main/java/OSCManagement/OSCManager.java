package OSCManagement;

import config.AppConfig;
import config.subconfig.Config;
import config.subconfig.OSCConfig;
import netP5.*;
import oscP5.OscMessage;
import oscP5.OscP5;

/**
 * Created by pierre on 07/11/15.
 */
public class OSCManager {

    private static OSCManager instance = new OSCManager();

    private OscP5 osc;

    private OSCManager() {
        osc = new OscP5( this , 12000);
    }

    public static OSCManager getInstance() {
        return instance;
    }

    public void sendOscMessage(Config config, Object... msgContents){
        for(OSCConfig cfg: config.getOSCConfig()) {
            System.out.println("send osc message");
            OscMessage message = new OscMessage(cfg.getOSCAdressPattern());
            message.add(msgContents);
            osc.send(message, new NetAddress(cfg.getOSCAddress(),Integer.parseInt(cfg.getOSCPort())));
        }
    }
}