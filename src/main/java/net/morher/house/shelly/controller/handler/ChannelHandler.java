package net.morher.house.shelly.controller.handler;

import java.io.Closeable;
import java.util.function.Consumer;
import net.morher.house.api.devicetypes.ElectricSensorDevice;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.EntityCategory;
import net.morher.house.api.entity.sensor.SensorOptions;
import net.morher.house.api.entity.sensor.SensorType;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.shelly.api.Channel;
import net.morher.house.shelly.api.ChannelReport;

public abstract class ChannelHandler implements Closeable {
  private final Subscription sub;
  private final Consumer<Double> currentSensor;
  private final Consumer<Double> powerSensor;
  private final Consumer<Double> voltageSensor;
  private final Consumer<Double> deviceTemperatureSensor;

  public ChannelHandler(Device device, Channel relay) {
    sub = relay.subscribeToChannelReport(this::onChannelReport);

    currentSensor =
        new LazySensor<>(device.entity(ElectricSensorDevice.CURRENT))
            .withOptions(new SensorOptions(SensorType.CURRENT));

    powerSensor =
        new LazySensor<>(device.entity(ElectricSensorDevice.POWER))
            .withOptions(new SensorOptions(SensorType.POWER));

    voltageSensor =
        new LazySensor<>(device.entity(ElectricSensorDevice.VOLTAGE))
            .withOptions(new SensorOptions(SensorType.VOLTAGE));

    deviceTemperatureSensor =
        new LazySensor<>(device.entity(GeneralDevice.DEVICE_TEMPERATURE))
            .withOptions(new SensorOptions(SensorType.TEMPERATURE_C, EntityCategory.DIAGNOSTIC));
  }

  private void onChannelReport(ChannelReport report) {
    currentSensor.accept(report.getCurrent());
    powerSensor.accept(report.getPower());
    voltageSensor.accept(report.getVoltage());
    deviceTemperatureSensor.accept(report.getTemperature());
  }

  @Override
  public void close() {
    sub.unsubscribe();
  }
}
