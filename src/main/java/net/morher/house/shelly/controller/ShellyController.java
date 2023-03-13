package net.morher.house.shelly.controller;

import static net.morher.house.api.config.DeviceName.combine;
import net.morher.house.api.config.DeviceName;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.shelly.api.Cover;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.Sensor;
import net.morher.house.shelly.api.ShellyNode;
import net.morher.house.shelly.config.ShellyConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyCoverConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyNodeConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyRelayConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellySensorConfig;

public class ShellyController {
  private final HouseMqttClient client;
  private final DeviceManager deviceManager;

  public ShellyController(HouseMqttClient client, DeviceManager deviceManager) {
    this.client = client;
    this.deviceManager = deviceManager;
  }

  public void configure(ShellyConfig config) {
    config.getNodes().forEach(this::configureNode);
  }

  private void configureNode(String nodeName, ShellyNodeConfig config) {
    ShellyNode node = config.getApi().getNode(client, nodeName);
    DeviceName deviceName = new DeviceName(config.getRoom(), null);

    configureRelay(node, 0, deviceName, config.getRelay0());
    configureRelay(node, 1, deviceName, config.getRelay1());
    configureCover(node, 0, deviceName, config.getCover());
    configureSensor(node, deviceName, config.getSensor());
  }

  private void configureSensor(
      ShellyNode node, DeviceName nodeName, ShellySensorConfig sensorConfig) {
    if (sensorConfig == null) {
      return;
    }
    DeviceId deviceId = combine(sensorConfig.getDevice(), nodeName).toDeviceId();
    ShellyDevice device = createDevice(deviceId);
    Sensor sensor = node.getSensor();
    device.addSensor(sensor, sensorConfig);
  }

  private void configureRelay(
      ShellyNode node, int relayIndex, DeviceName nodeName, ShellyRelayConfig relayConfig) {
    if (relayConfig == null) {
      return;
    }
    DeviceId deviceId = combine(relayConfig.getDevice(), nodeName).toDeviceId();
    ShellyDevice device = createDevice(deviceId);

    Relay relay = node.getRelay(relayIndex);

    switch (relayConfig.getAs()) {
      case LAMP:
        device.addLamp(relay);
        break;

      case SWITCH:
        device.addSwitch(relay);
        break;
    }
  }

  private void configureCover(
      ShellyNode node, int coverIndex, DeviceName nodeName, ShellyCoverConfig coverConfig) {
    if (coverConfig == null) {
      return;
    }
    Cover cover = node.getCover(coverIndex);

    DeviceId deviceId = combine(coverConfig.getDevice(), nodeName).toDeviceId();
    ShellyDevice device = createDevice(deviceId);
    device.addCover(cover, coverConfig);
  }

  public ShellyDevice createDevice(DeviceId deviceId) {
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Shelly");

    Device device = deviceManager.device(deviceId);
    device.setDeviceInfo(deviceInfo);
    return new ShellyDevice(device);
  }
}
