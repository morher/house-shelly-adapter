package net.morher.house.shelly.api.gen1;

import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.BooleanMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.ChannelReportListener;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.RelayStateListener;

public class Gen1Relay implements Relay {
  private final Topic<?> nodeTopic;
  private final Topic<Boolean> relayTopic;
  private final Topic<Boolean> commandTopic;

  public Gen1Relay(Topic<?> nodeTopic, int relayIndex) {
    this.nodeTopic = nodeTopic;
    this.relayTopic = nodeTopic.subTopic("/relay/" + relayIndex, BooleanMessage.onOffLowerCase());
    this.commandTopic = relayTopic.subTopic("/command", BooleanMessage.onOffLowerCase(), false);
  }

  @Override
  public void setState(boolean state) {
    commandTopic.publish(state);
  }

  @Override
  public Subscription subscribeToStateUpdate(RelayStateListener listener) {
    return relayTopic.subscribe(listener::onRelaysStateUpdate);
  }

  @Override
  public Subscription subscribeToChannelReport(ChannelReportListener listener) {
    return new Gen1ChannelReportSubscription(listener, relayTopic, nodeTopic);
  }
}
