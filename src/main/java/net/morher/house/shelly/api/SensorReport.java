package net.morher.house.shelly.api;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@Builder
public class SensorReport {
  private Double temperature;
  private Double illuminance;
  private Double tilt;
  private Boolean opening;
  private Boolean vibrationDetected;
  private Boolean motionDetected;
  private Boolean smokeDetected;
  private Boolean moistureDetected;
}
