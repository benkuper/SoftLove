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

    private Config config;
    private OscP5 osc;

    public OSCManager(Config config) {
        this.config = config;
        osc = new OscP5( this , 12000 );
    }

    public void sendOscMessage(Object... msgContents){
        for(OSCConfig cfg: config.getOSCConfig()) {
            System.out.println("send osc message");
            OscMessage message = new OscMessage(cfg.getOSCAdressPattern());
            message.add(msgContents);
            osc.send(message, new NetAddress(cfg.getOSCAddress(),Integer.parseInt(cfg.getOSCPort())));
        }
    }
}