package net.morher.house.shelly.api.gen1;

import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.EnumMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.ChannelReportListener;
import net.morher.house.shelly.api.Cover;

public class Gen1Cover implements Cover {
  private final Topic<?> nodeTopic;
  private final Topic<?> coverTopic;
  private final Topic<CoverCommand> commandTopic;

  public Gen1Cover(Topic<?> nodeTopic, int coverIndex) {
    this.nodeTopic = nodeTopic;
    this.coverTopic = nodeTopic.subTopic("/roller/" + coverIndex, null);
    this.commandTopic =
        coverTopic.subTopic("/command", EnumMessage.lowercase(CoverCommand.class), false);
  }

  @Override
  public void openCover() {
    commandTopic.publish(CoverCommand.OPEN);
  }

  @Override
  public void closeCover() {
    commandTopic.publish(CoverCommand.CLOSE);
  }

  @Override
  public void stopCover() {
    commandTopic.publish(CoverCommand.STOP);
  }

  @Override
  public Subscription subscribeToChannelReport(ChannelReportListener listener) {
    return new Gen1ChannelReportSubscription(listener, coverTopic, nodeTopic);
  }

  public static enum CoverCommand {
    OPEN,
    STOP,
    CLOSE;
  }
}
