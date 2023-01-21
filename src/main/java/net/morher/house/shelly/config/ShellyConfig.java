package net.morher.house.shelly.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import net.morher.house.api.config.DeviceName;

@Data
public class ShellyConfig {
  private Map<String, ShellyNodeConfig> nodes = new HashMap<>();

  @Data
  public static class ShellyNodeConfig {
    private ShellyRelayConfig relay0;
    private ShellyRelayConfig relay1;
    private ShellyCoverConfig cover;
  }

  @Data
  public static class ShellyRelayConfig {
    private ShellyLampConfig lamp;

    @JsonProperty("switch")
    private ShellySwitchConfig switchConfig;
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
}
