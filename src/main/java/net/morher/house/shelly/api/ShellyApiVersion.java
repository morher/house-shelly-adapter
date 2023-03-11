package net.morher.house.shelly.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.shelly.api.gen1.Gen1Node;
import net.morher.house.shelly.api.gen2.Gen2Node;

public enum ShellyApiVersion {
  GEN1 {
    @Override
    public ShellyNode getNode(HouseMqttClient mqtt, String name) {
      return new Gen1Node(mqtt.topic("shellies/" + name, null));
    }
  },

  GEN2 {
    @Override
    public ShellyNode getNode(HouseMqttClient mqtt, String name) {
      return new Gen2Node(mqtt.topic("shellies/" + name, null));
    }
  };

  public abstract ShellyNode getNode(HouseMqttClient mqtt, String name);

  @JsonCreator
  public static ShellyApiVersion fromString(String str) {
    return str != null ? ShellyApiVersion.valueOf(str.toUpperCase().replace(" ", "_")) : null;
  }
}
