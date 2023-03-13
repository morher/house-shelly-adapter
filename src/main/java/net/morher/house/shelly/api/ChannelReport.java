package net.morher.house.shelly.api;

import static java.util.Optional.ofNullable;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@Builder
public class ChannelReport {
  private final Double power;
  private final Double current;
  private final Double voltage;
  private final Double energy;
  private final Double temperature;

  public ChannelReport combine(ChannelReport report) {
    return ChannelReport.builder()
        .power(ofNullable(report.getPower()).orElse(power))
        .current(ofNullable(report.getCurrent()).orElse(current))
        .voltage(ofNullable(report.getVoltage()).orElse(voltage))
        .energy(ofNullable(report.getEnergy()).orElse(energy))
        .temperature(ofNullable(report.getTemperature()).orElse(temperature))
        .build();
  }
}
