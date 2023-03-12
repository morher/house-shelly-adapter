package net.morher.house.shelly.controller;

import static net.morher.house.api.config.DeviceName.combine;
import net.morher.house.api.config.DeviceName;
import net.morher.house.api.devicetypes.CoverDevice;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.cover.CoverEntity;
import net.morher.house.api.entity.cover.CoverOptions;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.shelly.api.Cover;
import net.morher.house.shelly.api.Relay;
import net.morher.house.shelly.api.ShellyNode;
import net.morher.house.shelly.config.ShellyConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyCoverConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyNodeConfig;
import net.morher.house.shelly.config.ShellyConfig.ShellyRelayConfig;

public class ShellyController {
  private final HouseMqttClient client;
  private final DeviceManager deviceManager;
  private final ResourceManager resources = new ResourceManager();

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

    configureCover(
        cover,
        coverConfig.isClosedAsSwitch(),
        combine(coverConfig.getDevice(), nodeName).toDeviceId());
  }

  private void configureCover(Cover cover, boolean closedAsSwitch, DeviceId deviceId) {
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Shelly");

    Device device = deviceManager.device(deviceId);
    device.setDeviceInfo(deviceInfo);

    CoverEntity coverEntity = device.entity(CoverDevice.COVER, new CoverOptions());
    new ShellyCover(cover, coverEntity);

    if (closedAsSwitch) {
      resources.add(
          new ShellyCoverSwitch(
              coverEntity, device.entity(GeneralDevice.ENABLE, new SwitchOptions())));
    }
  }

  public ShellyDevice createDevice(DeviceId deviceId) {
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Shelly");

    Device device = deviceManager.device(deviceId);
    device.setDeviceInfo(deviceInfo);
    return new ShellyDevice(device);
  }
}
