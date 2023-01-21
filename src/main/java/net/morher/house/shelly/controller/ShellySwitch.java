package net.morher.house.shelly.controller;

import java.io.Closeable;
import java.io.IOException;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.entity.switches.SwitchStateHandler;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.BooleanMessage;
import net.morher.house.api.subscription.Subscription;

public class ShellySwitch implements Closeable {
  private final Topic<Boolean> commandTopic;
  private final Subscription stateSubscription;
  private final SwitchStateHandler handler;

  public ShellySwitch(HouseMqttClient mqtt, String nodeName, int relayIndex, SwitchEntity entity) {
    String relayTopic = "shellies/" + nodeName + "/relay/" + relayIndex;

    handler = new SwitchStateHandler(entity, this::onLightState);
    entity.setOptions(new SwitchOptions());

    commandTopic = mqtt.topic(relayTopic + "/command", BooleanMessage.onOffLowerCase(), false);
    stateSubscription =
        mqtt.topic(relayTopic, BooleanMessage.onOffLowerCase()).subscribe(handler::updateState);
  }

  private void onLightState(Boolean lampState) {
    commandTopic.publish(lampState);
  }

  @Override
  public void close() throws IOException {
    stateSubscription.unsubscribe();
  }
}
