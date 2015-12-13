package OSCManagement;

import config.AppConfig;
import config.ConfigManager;
import config.subconfig.Config;
import config.subconfig.OSCConfig;
import netP5.*;
import org.zeromq.ZMQ;
import oscP5.OscMessage;
import oscP5.OscP5;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

/**
 * Created by jbaron
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

    void oscEvent(OscMessage theOscMessage) {

        // retrieve the number of recipients
        int numberOfRecipients = theOscMessage.get(0).intValue();

        // get the number of messages to transmit
        int numOfMsgs = theOscMessage.get(numberOfRecipients+1).intValue();

        for(int i=1;i<=numberOfRecipients;++i) {

            Config iConfig = ConfigManager.getInstance().getAppConfig().getConfig(theOscMessage.get(i).stringValue());

            String re1 = ".*?";    // Non-greedy match on filler
            // IPv4 IP Address 1
            String re2 = "((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?![\\d])";

            Pattern p = Pattern.compile(re1 + re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(iConfig.getSourceAddress());
            String ipaddress1;
            if (m.find()) {
                ipaddress1 = m.group(1);
                System.out.print("envoi vers : " + ipaddress1.toString() + "\n");
                // ipList.add(ipaddress1);
            }

            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket publisher = context.socket(ZMQ.PUB);
            publisher.bind(iConfig.getSourceAddress());

            publisher.sendMore(iConfig.getSourceTopic());
            for(int j=0;j<numOfMsgs;j++) {
                publisher.send(theOscMessage.get(numberOfRecipients+numOfMsgs+1+j).toString());
            }
        }
    }
}