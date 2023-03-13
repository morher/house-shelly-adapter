package net.morher.house.shelly.controller.handler;

import java.io.Closeable;
import java.io.IOException;
import net.morher.house.api.entity.sensor.BinarySensorEntity;
import net.morher.house.api.entity.sensor.BinarySensorOptions;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.Channel;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.shelly.config.ShellyConfig.ThresholdConfig;
import net.morher.house.shelly.config.ShellyConfig.ThresholdSensorConfig;

public class ThresholdHandler implements Closeable {
  private final BinarySensorEntity entity;
  private final ThresholdSensorConfig threshold;
  private final Subscription subscription;
  private ChannelReport report = ChannelReport.builder().build();

  public ThresholdHandler(
      BinarySensorEntity entity, Channel channel, ThresholdSensorConfig threshold) {
    this.entity = entity;
    this.threshold = threshold;
    entity.setOptions(new BinarySensorOptions(threshold.binarySensorType()));

    subscription = channel.subscribeToChannelReport(this::onChannelReport);
  }

  private void onChannelReport(ChannelReport newReport) {
    this.report = this.report.combine(newReport);

    entity.state().publish(checkThreshold());
  }

  private boolean checkThreshold() {
    return checkValue(report.getPower(), threshold.getPower())
        && checkValue(report.getCurrent(), threshold.getCurrent())
        && checkValue(report.getVoltage(), threshold.getVoltage())
        && checkValue(report.getTemperature(), threshold.getTemperature());
  }

  private boolean checkValue(Double value, ThresholdConfig levels) {
    if (levels != null) {
      if (levels.getMin() != null && (value == null || value < levels.getMin())) {
        return false;
      }
      if (levels.getMax() != null && (value == null || value > levels.getMax())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void close() throws IOException {
    subscription.close();
  }
}
