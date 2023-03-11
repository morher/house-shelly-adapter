package net.morher.house.shelly.api.gen2;

import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.shelly.api.ChannelReportListener;
import net.morher.house.shelly.api.RelayStateListener;

public class Gen2RelaySubscription implements Subscription {
  private final RelayStateListener stateListener;
  private final ChannelReportListener reportListener;
  private final Subscription statusSubscription;

  public Gen2RelaySubscription(
      Topic<Gen2RelayStatus> topic,
      RelayStateListener stateListener,
      ChannelReportListener reportListener) {
    this.stateListener = stateListener;
    this.reportListener = reportListener;

    this.statusSubscription = topic.subscribe(this::onRelayState);
  }

  private void onRelayState(Gen2RelayStatus status) {
    if (stateListener != null && status.getOutput() != null) {
      stateListener.onRelaysStateUpdate(status.getOutput());
    }
    if (reportListener != null) {
      reportListener.onChannelReport(
          ChannelReport.builder()
              .current(status.getCurrent())
              .power(status.getApower())
              .voltage(status.getVoltage())
              .energy(status.getAenergy().getTotal())
              .temperature(status.getTemperature().getCelsius())
              .build());
    }
  }

  @Override
  public void unsubscribe() {
    statusSubscription.unsubscribe();
  }
}
