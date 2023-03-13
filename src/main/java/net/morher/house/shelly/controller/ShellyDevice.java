package net.morher.house.shelly.controller;

import java.io.Closeable;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.morher.house.api.devicetypes.CoverDevice;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.EntityDefinition;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.entity.common.EntityOptions;
import net.morher.house.api.entity.common.StatefullEntity;
import net.morher.house.api.entity.cover.CoverEntity;
import net.morher.house.api.entity.cover.CoverOptions;
import net.morher.house.api.entity.sensor.BinarySensorEntity;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.shelly.api.Channel;
import net.morher.house.shelly.api.Cover;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.Sensor;
import net.morher.house.shelly.config.ShellyConfig.ShellyCoverConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyRelayConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellySensorConfig;
import net.morher.house.shelly.config.ShellyConfig.ThresholdSensorConfig;
import net.morher.house.shelly.controller.handler.CoverHandler;
import net.morher.house.shelly.controller.handler.LampHandler;
import net.morher.house.shelly.controller.handler.SensorHandler;
import net.morher.house.shelly.controller.handler.SwitchHandler;
import net.morher.house.shelly.controller.handler.ThresholdHandler;

public class ShellyDevice {
  private final ResourceManager resources = new ResourceManager();
  private final Device device;
  private Closeable handler;

  public ShellyDevice(Device device) {
    this.device = device;
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Shelly");
    device.setDeviceInfo(deviceInfo);
  }

  public void addLamp(Relay relay, ShellyRelayConfig config) {
    if (handler != null) {
      throw new IllegalStateException("A relay handler is already registered");
    }
    handler = new LampHandler(device, relay);
    addThresholds(relay, config.getThresholds());
  }

  public void addSwitch(Relay relay, ShellyRelayConfig config) {
    if (handler != null) {
      throw new IllegalStateException("A relay handler is already registered");
    }
    handler = new SwitchHandler(device, relay);
    addThresholds(relay, config.getThresholds());
  }

  public void addCover(Cover cover, ShellyCoverConfig config) {
    CoverEntity coverEntity = device.entity(CoverDevice.COVER, new CoverOptions());
    handler = new CoverHandler(device, cover);

    if (config.isClosedAsSwitch()) {
      resources.add(
          new ShellyCoverSwitch(
              coverEntity, device.entity(GeneralDevice.ENABLE, new SwitchOptions())));
    }
    addThresholds(cover, config.getThresholds());
  }

  private void addThresholds(Channel channel, Map<String, ThresholdSensorConfig> thresholds) {
    for (Map.Entry<String, ThresholdSensorConfig> threshold : thresholds.entrySet()) {
      addThreshold(channel, threshold.getKey(), threshold.getValue());
    }
  }

  private void addThreshold(Channel channel, String name, ThresholdSensorConfig config) {
    BinarySensorEntity entity =
        device.entity(new EntityDefinition<>(name, EntityManager::binarySensorEntity));
    resources.add(new ThresholdHandler(entity, channel, config));
  }

  public void addSensor(Sensor sensor, ShellySensorConfig sensorConfig) {
    resources.add(new SensorHandler(device, sensor, sensorConfig));
  }

  @RequiredArgsConstructor
  private class LazySensor<S, O extends EntityOptions, E extends StatefullEntity<S, O>>
      implements Consumer<S> {
    private final EntityDefinition<E> def;
    private final O options;
    private E entity;

    @Override
    public void accept(S value) {
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
