package net.morher.house.shelly.controller;

import java.io.Closeable;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightOptions;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.entity.light.LightState.PowerState;
import net.morher.house.api.entity.light.LightStateHandler;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.BooleanMessage;
import net.morher.house.api.subscription.Subscription;

public class ShellyLamp implements Closeable {
  private final Topic<Boolean> commandTopic;
  private final Subscription stateSubscription;
  private LightStateHandler handler;

  public ShellyLamp(
      HouseMqttClient mqtt, String nodeName, int relayIndex, LightEntity lightEntity) {
    String relayTopic = "shellies/" + nodeName + "/relay/" + relayIndex;
    commandTopic = mqtt.topic(relayTopic + "/command", BooleanMessage.onOffLowerCase(), false);
    stateSubscription =
        mqtt.topic(relayTopic, BooleanMessage.onOffLowerCase())
            .subscribe(this::onRelayStateChanged);

    handler = new LightStateHandler(lightEntity, this::onLightState);
    lightEntity.setOptions(new LightOptions(false, null));
  }

  public void onLightState(LightState lampState) {
    commandTopic.publish(PowerState.ON.equals(lampState.getState()));
  }

  public void onRelayStateChanged(Boolean data) {
    handler.updateState(new LightState(data ? PowerState.ON : PowerState.OFF, null, null));
  }

  @Override
  public void close() {
    stateSubscription.close();
  }
}
