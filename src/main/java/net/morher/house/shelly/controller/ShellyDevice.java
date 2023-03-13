package net.morher.house.shelly.controller;

import java.io.Closeable;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.morher.house.api.devicetypes.CoverDevice;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.EntityDefinition;
import net.morher.house.api.entity.common.EntityOptions;
import net.morher.house.api.entity.common.StatefullEntity;
import net.morher.house.api.entity.cover.CoverEntity;
import net.morher.house.api.entity.cover.CoverOptions;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.shelly.api.Cover;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.Sensor;
import net.morher.house.shelly.config.ShellyConfig.ShellyCoverConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellySensorConfig;
import net.morher.house.shelly.controller.handler.CoverHandler;
import net.morher.house.shelly.controller.handler.LampHandler;
import net.morher.house.shelly.controller.handler.SensorHandler;
import net.morher.house.shelly.controller.handler.SwitchHandler;

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

  public void addLamp(Relay relay) {
    if (handler != null) {
      throw new IllegalStateException("A relay handler is already registered");
    }
    handler = new LampHandler(device, relay);
  }

  public void addSwitch(Relay relay) {
    if (handler != null) {
      throw new IllegalStateException("A relay handler is already registered");
    }
    handler = new SwitchHandler(device, relay);
  }

  public void addCover(Cover cover, ShellyCoverConfig config) {
    CoverEntity coverEntity = device.entity(CoverDevice.COVER, new CoverOptions());
    handler = new CoverHandler(device, cover);

    if (config.isClosedAsSwitch()) {
      resources.add(
          new ShellyCoverSwitch(
              coverEntity, device.entity(GeneralDevice.ENABLE, new SwitchOptions())));
    }
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
