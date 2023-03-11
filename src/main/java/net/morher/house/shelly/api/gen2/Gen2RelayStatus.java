package net.morher.house.shelly.api.gen2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gen2RelayStatus {
  private Boolean output;
  private Double apower;
  private Double current;
  private Double voltage;
  private final Energy aenergy = new Energy();
  private final Temperature temperature = new Temperature();

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Energy {
    private Double total;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Temperature {
    @JsonProperty("tC")
    private Double celsius;
  }
}
