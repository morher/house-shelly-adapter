package net.morher.house.shelly.api.gen1;

import lombok.RequiredArgsConstructor;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.shelly.api.Cover;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.Sensor;
import net.morher.house.shelly.api.ShellyNode;

@RequiredArgsConstructor
public class Gen1Node implements ShellyNode {
  private final Topic<?> nodeTopic;

  @Override
  public Relay getRelay(int relayIndex) {
    return new Gen1Relay(nodeTopic, relayIndex);
  }

  @Override
  public Cover getCover(int coverIndex) {
    return new Gen1Cover(nodeTopic, coverIndex);
  }

  @Override
  public Sensor getSensor() {
    return new Gen1Sensor(nodeTopic);
  }
}
