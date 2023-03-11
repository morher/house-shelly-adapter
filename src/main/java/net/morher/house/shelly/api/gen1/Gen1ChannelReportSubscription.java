package net.morher.house.shelly.api.gen1;

import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.NumberMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.api.utils.ResourceManager.ConstructionBlock;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.shelly.api.ChannelReportListener;

public class Gen1ChannelReportSubscription implements Subscription {
  private static final ChannelReport REPORT = ChannelReport.builder().build();
  private final ResourceManager resources = new ResourceManager();

  public Gen1ChannelReportSubscription(
      ChannelReportListener listener, Topic<?> channelTopic, Topic<?> nodeTopic) {
    try (ConstructionBlock cb = resources.constructionBlock()) {
      // Channel topics:
      resources.add(
          channelTopic
              .subTopic("/power", NumberMessage.decimal())
              .subscribe(power -> listener.onChannelReport(REPORT.withPower(power))));
      resources.add(
          channelTopic
              .subTopic("/energy", NumberMessage.decimal())
              .subscribe(energy -> listener.onChannelReport(REPORT.withEnergy(energy))));

      // Node topics:
      resources.add(
          nodeTopic
              .subTopic("/voltage", NumberMessage.decimal())
              .subscribe(voltage -> listener.onChannelReport(REPORT.withVoltage(voltage))));
      resources.add(
          nodeTopic
              .subTopic("/temperature", NumberMessage.decimal())
              .subscribe(
                  temperature -> listener.onChannelReport(REPORT.withTemperature(temperature))));

      cb.complete();
    }
  }

  @Override
  public void unsubscribe() {
    resources.closeQuietly();
  }
}
