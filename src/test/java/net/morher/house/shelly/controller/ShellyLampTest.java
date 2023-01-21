package net.morher.house.shelly.controller;

import static net.morher.house.api.entity.light.LightState.PowerState.ON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.entity.common.StatefullEntity;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.MqttMessageListener;
import net.morher.house.api.mqtt.payload.PayloadFormat;
import net.morher.house.api.mqtt.payload.RawMessage;
import net.morher.house.test.client.TestHouseMqttClient;
import org.junit.Test;

public class ShellyLampTest {

  private HouseMqttClient mqtt = TestHouseMqttClient.loopback();
  private EntityManager entityManager = new EntityManager(mqtt);
  private DeviceManager deviceManager = new DeviceManager(entityManager);

  @Test
  public void testReactToEntityCommand() {
    List<String> commands = shellyCommandCollector("test-shelly", 1);

    LightEntity lightEntity =
        deviceManager.device(new DeviceId("Room", "Device")).entity(LampDevice.LIGHT);

    new ShellyLamp(mqtt, "test-shelly", 1, lightEntity);

    lightEntity.sendCommand(new LightState(ON, null, null));

    assertThat(commands.size(), is(1));
    assertThat(commands.get(0), is(equalTo("on")));
  }

  @Test
  public void updateLightStateWhenRelayStateChanges() {
    LightEntity lightEntity =
        deviceManager.device(new DeviceId("Room", "Device")).entity(LampDevice.LIGHT);

    List<LightState> stateCollector = stateCollector(lightEntity);

    new ShellyLamp(mqtt, "test-shelly", 1, lightEntity);

    mqtt.publish("shellies/test-shelly/relay/1", "on".getBytes(), false);

    assertThat(stateCollector.size(), is(1));
    assertThat(stateCollector.get(0), is(equalTo(new LightState(ON, null, null))));
  }

  private <S> List<S> stateCollector(StatefullEntity<S, ?> entity) {
    List<S> states = new ArrayList<>();
    entity.state().subscribe(states::add);
    return states;
  }

  private List<String> shellyCommandCollector(String nodeName, int relayIndex) {
    return collector(
        "shellies/" + nodeName + "/relay/" + relayIndex + "/command", RawMessage.toStr());
  }

  private <T> List<T> collector(String topic, PayloadFormat<T> mapper) {
    List<T> messages = new ArrayList<>();
    mqtt.subscribe(topic, MqttMessageListener.map(mapper).thenNotify(messages::add));
    return messages;
  }
}
