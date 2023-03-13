package net.morher.house.shelly.controller.handler;

import java.io.Closeable;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.entity.switches.SwitchStateHandler;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.api.utils.ResourceManager.ConstructionBlock;
import net.morher.house.shelly.api.Relay;

public class SwitchHandler extends ChannelHandler implements Closeable {
  private final SwitchStateHandler handler;
  private final ResourceManager resources = new ResourceManager();

  public SwitchHandler(Device device, Relay relay) {
    super(device, relay);
    try (ConstructionBlock cb = resources.constructionBlock()) {
      SwitchEntity entity = device.entity(GeneralDevice.POWER);
      handler = new SwitchStateHandler(entity, relay::setState);
      entity.setOptions(new SwitchOptions());

      resources.add(relay.subscribeToStateUpdate(handler::updateState));

      cb.complete();
    }
  }

  @Override
  public void close() {
    super.close();
    resources.closeQuietly();
    handler.disconnect();
  }
}
