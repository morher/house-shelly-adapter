package net.morher.house.shelly.api.gen1;

import lombok.RequiredArgsConstructor;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.BooleanMessage;
import net.morher.house.api.mqtt.payload.JsonMessage;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.api.utils.ResourceManager.ConstructionBlock;
import net.morher.house.shelly.api.Sensor;
import net.morher.house.shelly.api.SensorReport;
import net.morher.house.shelly.api.SensorReportListener;

@RequiredArgsConstructor
public class Gen1Sensor implements Sensor {
  private static final SensorReport REPORT = SensorReport.builder().build();
  private final Topic<?> nodeTopic;

  @Override
  public Subscription subscribeToSensorReport(SensorReportListener listener) {
    return new Gen1SensorSubscription(listener, nodeTopic);
  }

  private static class Gen1SensorSubscription implements Subscription {
    private final ResourceManager resources = new ResourceManager();

    public Gen1SensorSubscription(SensorReportListener listener, Topic<?> nodeTopic) {
      try (ConstructionBlock cb = resources.constructionBlock()) {
        resources.add(
            nodeTopic
                .subTopic("/info", JsonMessage.toType(Gen1SensorInfo.class))
                .subscribe(
                    value ->
                        listener.onSensorReport(
                            SensorReport.builder()
                                .temperature(value.getTemperature().getCelsius())
                                .illuminance(value.getIlluminance().getLux())
                                .tilt(value.getTilt().getDegrees())
                                .opening(value.getSensor().isOpen())
                                .vibrationDetected(value.getSensor().getVibration())
                                .motionDetected(value.getSensor().getMotion())
                                .build())));

        resources.add(
            nodeTopic
                .subTopic("/sensor/smoke", BooleanMessage.onOff())
                .subscribe(value -> listener.onSensorReport(REPORT.withSmokeDetected(value))));

        resources.add(
            nodeTopic
                .subTopic("/sensor/moisture", BooleanMessage.onOff())
                .subscribe(value -> listener.onSensorReport(REPORT.withSmokeDetected(value))));

        cb.complete();
      }
    }

    @Override
    public void unsubscribe() {
      resources.closeQuietly();
    }
  }
}
