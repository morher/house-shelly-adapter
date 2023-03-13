package net.morher.house.shelly.controller;

import static net.morher.house.api.entity.light.LightState.PowerState.ON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.List;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.entity.common.StatefullEntity;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.shelly.api.TestRelay;
import net.morher.house.shelly.controller.handler.LampHandler;
import net.morher.house.test.client.TestHouseMqttClient;
import org.junit.Test;

public class ShellyLampTest {

  private HouseMqttClient mqtt = TestHouseMqttClient.loopback();
  private EntityManager entityManager = new EntityManager(mqtt);
  private DeviceManager deviceManager = new DeviceManager(entityManager);

  private final TestRelay relay = new TestRelay();

  @Test
  public void testReactToEntityCommand() {
    Device device = deviceManager.device(new DeviceId("Room", "Device"));
    LightEntity lightEntity = device.entity(LampDevice.LIGHT);

    new LampHandler(device, relay);

    lightEntity.sendCommand(new LightState(ON, null, null));

    assertThat(relay.getStateCommands(), hasItems(true));
  }

  @Test
  public void updateLightStateWhenRelayStateChanges() {
    Device device = deviceManager.device(new DeviceId("Room", "Device"));
    LightEntity lightEntity = device.entity(LampDevice.LIGHT);

    List<LightState> stateCollector = stateCollector(lightEntity);

    new LampHandler(device, relay);

    relay.getStateUpdateListeners().forEach(l -> l.onRelaysStateUpdate(true));

    assertThat(stateCollector.size(), is(1));
    assertThat(stateCollector.get(0), is(equalTo(new LightState(ON, null, null))));
  }

  private <S> List<S> stateCollector(StatefullEntity<S, ?> entity) {
    List<S> states = new ArrayList<>();
    entity.state().subscribe(states::add);
    return states;
  }
}
