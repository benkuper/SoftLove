package config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Class that can serialize or deserialize an AppConfig
 *
 */
public class ConfigManager {

    private static ConfigManager instance = new ConfigManager();

    private ConfigManager(){
        theAppConfig = loadConfig("config.json");
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    private static AppConfig theAppConfig = null;

    public AppConfig getAppConfig() {
        return theAppConfig;
    }

    public void saveConfig(AppConfig config){
        ObjectMapper mapper = new ObjectMapper();

        //Object to JSON in file
        try {
            mapper.writeValue(new File("save.json"), config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AppConfig loadConfig(String fileName){
        File file = new File(fileName);

        ObjectMapper mapper = new ObjectMapper();
        try {
           AppConfig appConfig = mapper.readValue(file, AppConfig.class);
            theAppConfig = appConfig;
            return appConfig;
        } catch (IOException e) {
            System.err.println("Error : " + fileName + " not found in path");
            e.printStackTrace();
        }
        return null;
    }
}
