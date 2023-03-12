package net.morher.house.shelly.controller;

import java.io.Closeable;
import lombok.RequiredArgsConstructor;
import net.morher.house.api.devicetypes.ElectricSensorDevice;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.EntityCategory;
import net.morher.house.api.entity.EntityDefinition;
import net.morher.house.api.entity.sensor.SensorEntity;
import net.morher.house.api.entity.sensor.SensorOptions;
import net.morher.house.api.entity.sensor.SensorType;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.shelly.api.Relay;

public class ShellyDevice {
  private final ResourceManager resources = new ResourceManager();
  private final Device device;
  private final LazySensor<Double> currentSensor =
      new LazySensor<>(ElectricSensorDevice.CURRENT, new SensorOptions(SensorType.CURRENT));
  private final LazySensor<Double> powerSensor =
      new LazySensor<>(ElectricSensorDevice.POWER, new SensorOptions(SensorType.POWER));
  private final LazySensor<Double> voltageSensor =
      new LazySensor<>(ElectricSensorDevice.VOLTAGE, new SensorOptions(SensorType.VOLTAGE));
  private final LazySensor<Double> deviceTemperatureSensor =
      new LazySensor<>(
          GeneralDevice.DEVICE_TEMPERATURE,
          new SensorOptions(SensorType.TEMPERATURE_C, EntityCategory.DIAGNOSTIC));
  private Closeable relayHandler;

  public ShellyDevice(Device device) {
    this.device = device;
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Shelly");
    device.setDeviceInfo(deviceInfo);
  }

  public void addLamp(Relay relay) {
    if (relayHandler != null) {
      throw new IllegalStateException("A relay handler is already registered");
    }
    relayHandler = new ShellyLamp(relay, device.entity(LampDevice.LIGHT));
    resources.add(relay.subscribeToChannelReport(this::onChannelReport));
  }

  public void addSwitch(Relay relay) {
    if (relayHandler != null) {
      throw new IllegalStateException("A relay handler is already registered");
    }
    relayHandler = new ShellySwitch(relay, device.entity(GeneralDevice.POWER));
    resources.add(relay.subscribeToChannelReport(this::onChannelReport));
  }

  private void onChannelReport(ChannelReport report) {
    currentSensor.update(report.getCurrent());
    powerSensor.update(report.getPower());
    voltageSensor.update(report.getVoltage());
    deviceTemperatureSensor.update(report.getTemperature());
  }

  @RequiredArgsConstructor
  private class LazySensor<S> {
    private final EntityDefinition<? extends SensorEntity<S>> def;
    private final SensorOptions options;
    private SensorEntity<S> entity;

    public void update(S value) {
      if (value == null) {
        return;
      }
      if (entity == null) {
        entity = device.entity(def, options);
      }
      entity.state().publish(value);
    }
  }
}
