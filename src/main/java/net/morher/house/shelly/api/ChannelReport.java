package net.morher.house.shelly.api;

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
}
