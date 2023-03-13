package net.morher.house.shelly.api.gen1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gen1SensorInfo {
  @JsonProperty("tmp")
  private Temperature temperature = new Temperature();

  @JsonProperty("lux")
  private Illuminance illuminance = new Illuminance();

  @JsonProperty("accel")
  private final Tilt tilt = new Tilt();

  @JsonProperty("sensor")
  private final Sensor sensor = new Sensor();

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Temperature {
    @JsonProperty("tC")
    private Double celsius;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Illuminance {
    @JsonProperty("value")
    private Double lux;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Tilt {
    @JsonProperty("tilt")
    private Double degrees;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Sensor {
    @JsonProperty("state")
    private String opening;

    @JsonProperty("active")
    private Boolean motion;

    private Boolean vibration;

    public Boolean isOpen() {
      return opening != null ? "open".equals(opening) : null;
    }
  }
}
