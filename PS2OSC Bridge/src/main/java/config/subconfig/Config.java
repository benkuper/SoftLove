package config.subconfig;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Config {

    private String SourceAddress;
    private String SourceTopic;
    private List<config.subconfig.OSCConfig> OSCConfig = new ArrayList<config.subconfig.OSCConfig>();


    public Config() {
    }

    public Config(String SourceAddress, String SourceTopic, List<config.subconfig.OSCConfig> OSCConfig) {
        this.SourceAddress = SourceAddress;
        this.SourceTopic = SourceTopic;
        this.OSCConfig = OSCConfig;
    }

    @JsonProperty("SourceAddress")
    public String getSourceAddress() {
        return SourceAddress;
    }

    @JsonProperty("SourceAddress")
    public void setSourceAddress(String SourceAddress) {
        this.SourceAddress = SourceAddress;
    }

    @JsonProperty("SourceTopic")
    public String getSourceTopic() {
        return SourceTopic;
    }

    @JsonProperty("SourceTopic")
    public void setSourceTopic(String SourceTopic) {
        this.SourceTopic = SourceTopic;
    }

    @JsonProperty("OSC")
    public List<config.subconfig.OSCConfig> getOSCConfig() {
        return OSCConfig;
    }

    @JsonProperty("OSCConfig")
    public void setOSCConfig(List<config.subconfig.OSCConfig> OSCConfig) {
        this.OSCConfig = OSCConfig;
    }

    @Override
    public String toString() {
        return "Config{" +
                "SourceAddress='" + SourceAddress + '\'' +
                ", SourceTopic='" + SourceTopic + '\'' +
                ", OSCConfig=" + OSCConfig +
                '}';
    }
}
