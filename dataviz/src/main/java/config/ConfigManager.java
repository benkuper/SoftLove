package config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by pierre on 07/11/15.
 */
public class ConfigManager {

    public ConfigManager(){
    }

    public void saveConfig(Config config){
        ObjectMapper mapper = new ObjectMapper();

        //Object to JSON in file
        try {
            mapper.writeValue(new File("save.json"), config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config loadConfig(String fileName){
        File file = new File(fileName);

        ObjectMapper mapper = new ObjectMapper();
        try {
           Config loadedConfig = mapper.readValue(file, Config.class);
            return loadedConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
