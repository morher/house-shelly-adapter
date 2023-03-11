package net.morher.house.shelly.config;

import static net.morher.house.shelly.config.ShellyConfig.ExposeType.SWITCH;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import net.morher.house.api.config.DeviceName;
import net.morher.house.shelly.api.ShellyApiVersion;

@Data
public class ShellyConfig {
  private Map<String, ShellyNodeConfig> nodes = new HashMap<>();

  @Data
  public static class ShellyNodeConfig {
    private String room;
    private ShellyApiVersion api = ShellyApiVersion.GEN1;
    private ShellyRelayConfig relay0;
    private ShellyRelayConfig relay1;
    private ShellyCoverConfig cover;
  }

  @Data
  public static class ShellyRelayConfig {
    private DeviceName device;
    private ExposeType as = SWITCH;
    private Map<String, ThresholdSensorConfig> thresholds = new HashMap<>();

    @Deprecated private ShellyLampConfig lamp;

    @Deprecated
    @JsonProperty("switch")
    private ShellySwitchConfig switchConfig;
  }

  @Data
  public static class ThresholdSensorConfig {
    private ThresholdConfig power;
    private ThresholdConfig current;
    private ThresholdConfig temperature;
  }

  @Data
  public static class ThresholdConfig {
    private Double min;
    private Double max;
  }

  @Data
  public static class ShellyLampConfig {
    private DeviceName device;
  }

  @Data
  public static class ShellySwitchConfig {
    private DeviceName device;
  }

  @Data
  public static class ShellyCoverConfig {
    private DeviceName device;
    private boolean closedAsSwitch;
  }

  public static enum ExposeType {
    LAMP,
    SWITCH;

    @JsonCreator
    public static ExposeType fromString(String str) {
      return str != null ? ExposeType.valueOf(str.toUpperCase().replace(" ", "_")) : null;
    }
  }
}
