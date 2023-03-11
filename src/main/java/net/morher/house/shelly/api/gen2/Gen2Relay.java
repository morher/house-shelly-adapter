package net.morher.house.shelly.api.gen2;

import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.BooleanMessage;
import net.morher.house.api.mqtt.payload.JsonMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.ChannelReportListener;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.RelayStateListener;

public class Gen2Relay implements Relay {
  private final Topic<Gen2RelayStatus> relayTopic;
  private final Topic<Boolean> commandTopic;

  public Gen2Relay(Topic<?> nodeTopic, int relayIndex) {
    this.relayTopic =
        nodeTopic.subTopic(
            "/status/switch:" + relayIndex, JsonMessage.toType(Gen2RelayStatus.class));
    this.commandTopic =
        nodeTopic.subTopic("/command/switch:" + relayIndex, BooleanMessage.onOffLowerCase(), false);
  }

  @Override
  public void setState(boolean state) {
    commandTopic.publish(state);
  }

  @Override
  public Subscription subscribeToStateUpdate(RelayStateListener listener) {
    return new Gen2RelaySubscription(relayTopic, listener, null);
  }

  @Override
  public Subscription subscribeToChannelReport(ChannelReportListener listener) {
    return new Gen2RelaySubscription(relayTopic, null, listener);
  }
}
