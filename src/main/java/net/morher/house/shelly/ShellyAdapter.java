package net.morher.house.shelly;

import net.morher.house.api.context.HouseMqttContext;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.shelly.config.ShellyAdapterConfig;
import net.morher.house.shelly.controller.ShellyController;

public class ShellyAdapter {
  public static void main(String[] args) throws Exception {
    new ShellyAdapter().run(new HouseMqttContext("shelly-adapter"));
  }

  public void run(HouseMqttContext ctx) {
    HouseMqttClient client = ctx.client();
    DeviceManager deviceManager = ctx.deviceManager();

    new ShellyController(client, deviceManager)
        .configure(ctx.loadAdapterConfig(ShellyAdapterConfig.class).getShelly());
  }
}
