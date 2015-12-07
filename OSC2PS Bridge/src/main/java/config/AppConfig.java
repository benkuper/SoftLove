package config;

import config.subconfig.Config;
import java.util.HashMap;

public class AppConfig {
    public HashMap<String,Config> listConfig;

    public Config getConfig(String key){
        return listConfig.get(key);
    }

    public AppConfig() {
    }

    public AppConfig(HashMap<String, Config> listConfig) {
        this.listConfig = listConfig;
    }
}
