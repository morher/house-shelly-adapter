package net.morher.house.shelly.api.gen2;

import lombok.RequiredArgsConstructor;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.shelly.api.Cover;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.ShellyNode;

@RequiredArgsConstructor
public class Gen2Node implements ShellyNode {
  private final Topic<?> nodeTopic;

  @Override
  public Relay getRelay(int relayIndex) {
    return new Gen2Relay(nodeTopic, relayIndex);
  }

  @Override
  public Cover getCover(int coverIndex) {
    return new Gen2Cover(nodeTopic, coverIndex);
  }
}
