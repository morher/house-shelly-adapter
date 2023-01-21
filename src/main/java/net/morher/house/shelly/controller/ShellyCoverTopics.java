package net.morher.house.shelly.controller;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.EnumMessage;
import net.morher.house.api.mqtt.payload.NumberMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.controller.ShellyCover.ShellyCoverState;

@Slf4j
public class ShellyCoverTopics {
  private final Topic<ShellyCoverState> stateTopic;
  private final Topic<ShellyCoverState> commandTopic;
  private final Subscription powerSubscription;

  public ShellyCoverTopics(HouseMqttClient mqtt, String nodeName, Consumer<Double> powerListener) {
    stateTopic =
        mqtt.topic(
            "shellies/" + nodeName + "/roller/0", EnumMessage.lowercase(ShellyCoverState.class));

    commandTopic =
        stateTopic.subTopic("/command", EnumMessage.lowercase(ShellyCoverState.class), false);

    powerSubscription =
        stateTopic.subTopic("/power", NumberMessage.decimal()).subscribe(powerListener);
  }

  public void command(ShellyCoverState command) {
    log.debug("Send command to {}: {}", commandTopic.getTopic(), command);
    commandTopic.publish(command);
  }

  public void disconnect() {
    powerSubscription.unsubscribe();
  }
}
