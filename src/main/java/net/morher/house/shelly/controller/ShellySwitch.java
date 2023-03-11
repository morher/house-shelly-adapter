package net.morher.house.shelly.controller;

import java.io.Closeable;
import java.io.IOException;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.entity.switches.SwitchStateHandler;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.api.utils.ResourceManager.ConstructionBlock;
import net.morher.house.shelly.api.Relay;

public class ShellySwitch implements Closeable {
  private final SwitchStateHandler handler;
  private final ResourceManager resources = new ResourceManager();

  public ShellySwitch(Relay relay, SwitchEntity entity) {
    try (ConstructionBlock cb = resources.constructionBlock()) {
      handler = new SwitchStateHandler(entity, relay::setState);
      entity.setOptions(new SwitchOptions());

      resources.add(relay.subscribeToStateUpdate(handler::updateState));

      cb.complete();
    }
  }

  @Override
  public void close() throws IOException {
    resources.closeQuietly();
    handler.disconnect();
  }
}
