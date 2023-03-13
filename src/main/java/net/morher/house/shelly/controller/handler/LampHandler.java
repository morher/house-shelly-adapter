package net.morher.house.shelly.controller.handler;

import static net.morher.house.api.entity.light.LightState.PowerState.OFF;
import static net.morher.house.api.entity.light.LightState.PowerState.ON;
import java.io.Closeable;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightOptions;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.entity.light.LightStateHandler;
import net.morher.house.api.utils.ResourceManager;
import net.morher.house.api.utils.ResourceManager.ConstructionBlock;
import net.morher.house.shelly.api.Relay;

public class LampHandler extends ChannelHandler implements Closeable {
  private LightStateHandler handler;
  private ResourceManager resources = new ResourceManager();

  public LampHandler(Device device, Relay relay) {
    super(device, relay);
    LightEntity lightEntity = device.entity(LampDevice.LIGHT);
    try (ConstructionBlock cb = resources.constructionBlock()) {
      handler = new LightStateHandler(lightEntity, s -> relay.setState(ON.equals(s.getState())));
      lightEntity.setOptions(new LightOptions(false, null));
      relay.subscribeToStateUpdate(
          s -> handler.updateState(new LightState().withState(s ? ON : OFF)));

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
