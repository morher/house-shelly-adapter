package net.morher.house.shelly.api.gen2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.EnumMessage;
import net.morher.house.api.mqtt.payload.JsonMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.shelly.api.ChannelReportListener;
import net.morher.house.shelly.api.Cover;

public class Gen2Cover implements Cover {
  private final Topic<Command> commandTopic;
  private final Topic<Status> statusTopic;

  public Gen2Cover(Topic<?> nodeTopic, int coverIndex) {
    this.commandTopic =
        nodeTopic.subTopic(
            "/command/cover:" + coverIndex, EnumMessage.lowercase(Command.class), false);
    this.statusTopic =
        nodeTopic.subTopic("/status/cover:" + coverIndex, JsonMessage.toType(Status.class));
  }

  @Override
  public void openCover() {
    commandTopic.publish(Command.OPEN);
  }

  @Override
  public void closeCover() {
    commandTopic.publish(Command.CLOSE);
  }

  @Override
  public void stopCover() {
    commandTopic.publish(Command.STOP);
  }

  @Override
  public Subscription subscribeToChannelReport(ChannelReportListener listener) {
    return statusTopic.subscribe(s -> listener.onChannelReport(s.toChannelReport()));
  }

  private static enum Command {
    OPEN,
    CLOSE,
    STOP;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class Status {
    private Double apower;
    private Double current;
    private Double voltage;

    public ChannelReport toChannelReport() {
      return ChannelReport.builder().power(apower).current(current).voltage(voltage).build();
    }
  }
}
