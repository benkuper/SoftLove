package config.subconfig;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "OSCAddress",
    "OSCPort",
    "OSCAdressPattern"
})
public class OSCConfig {

    @JsonProperty("OSCAddress")
    private String OSCAddress;
    @JsonProperty("OSCPort")
    private String OSCPort;
    @JsonProperty("OSCAdressPattern")
    private String OSCAdressPattern;

    public OSCConfig() {
    }

    public OSCConfig(String OSCAddress, String OSCPort, String OSCAdressPattern) {
        this.OSCAddress = OSCAddress;
        this.OSCPort = OSCPort;
        this.OSCAdressPattern = OSCAdressPattern;
    }

    @JsonProperty("OSCAddress")
    public String getOSCAddress() {
        return OSCAddress;
    }


    @JsonProperty("OSCAddress")
    public void setOSCAddress(String OSCAddress) {
        this.OSCAddress = OSCAddress;
    }


    @JsonProperty("OSCPort")
    public String getOSCPort() {
        return OSCPort;
    }


    @JsonProperty("OSCPort")
    public void setOSCPort(String OSCPort) {
        this.OSCPort = OSCPort;
    }

    @JsonProperty("OSCAdressPattern")
    public String getOSCAdressPattern() {
        return OSCAdressPattern;
    }

    @JsonProperty("OSCAdressPattern")
    public void setOSCAdressPattern(String OSCAdressPattern) {
        this.OSCAdressPattern = OSCAdressPattern;
    }

    @Override
    public String toString() {
        return "OSCConfig{" +
                "OSCAddress='" + OSCAddress + '\'' +
                ", OSCPort='" + OSCPort + '\'' +
                ", OSCAdressPattern='" + OSCAdressPattern + '\'' +
                '}';
    }

}
