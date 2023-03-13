package net.morher.house.shelly.config;

import static net.morher.house.shelly.config.ShellyConfig.ExposeType.SWITCH;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.morher.house.api.config.DeviceName;
import net.morher.house.api.entity.sensor.BinarySensorType;
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
    private ShellySensorConfig sensor;
  }

  @Data
  public static class ShellySensorConfig {
    private DeviceName device;
    private OpeningType opening;
  }

  @Data
  public static class ShellyRelayConfig {
    private DeviceName device;
    private ExposeType as = SWITCH;
    private Map<String, ThresholdSensorConfig> thresholds = new HashMap<>();
  }

  @Data
  public static class ThresholdSensorConfig {
    private String type;
    private ThresholdConfig power;
    private ThresholdConfig current;
    private ThresholdConfig voltage;
    private ThresholdConfig temperature;

    public BinarySensorType binarySensorType() {
      return type != null
          ? BinarySensorType.valueOf(type.toUpperCase().replace(" ", "_"))
          : BinarySensorType.POWER;
    }
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
    private Map<String, ThresholdSensorConfig> thresholds = new HashMap<>();
  }

  public static enum ExposeType {
    LAMP,
    SWITCH;

    @JsonCreator
    public static ExposeType fromString(String str) {
      return str != null ? ExposeType.valueOf(str.toUpperCase().replace(" ", "_")) : null;
    }
  }

  @Getter
  @RequiredArgsConstructor
  public static enum OpeningType {
    DOOR(BinarySensorType.DOOR),
    WINDOW(BinarySensorType.WINDOW);

    @JsonCreator
    public static OpeningType fromString(String str) {
      return str != null ? OpeningType.valueOf(str.toUpperCase().replace(" ", "_")) : null;
    }

    private final BinarySensorType sensorType;
  }
}
