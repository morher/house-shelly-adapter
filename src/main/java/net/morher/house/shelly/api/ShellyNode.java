package net.morher.house.shelly.api;

public interface ShellyNode {
  Relay getRelay(int relayIndex);

  Cover getCover(int coverIndex);

  Sensor getSensor();
}
