package net.morher.house.shelly.api.gen1;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.List;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.RawMessage;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.test.client.TestHouseMqttClient;
import org.junit.Test;

public class Gen1RelayTest {
  private HouseMqttClient mqtt = TestHouseMqttClient.loopback();

  @Test
  public void testSetState() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/relay/0/command", RawMessage.toStr()).subscribe(commands::add);

    Gen1Relay relay = new Gen1Relay(nodeTopic, 0);

    relay.setState(true);
    assertThat(commands, hasItems("on"));

    relay.setState(false);
    assertThat(commands, hasItems("on", "off"));
  }

  @Test
  public void testStateUpdate() {
    Topic<Object> nodeTopic = mqtt.topic("shellies/test-node", null);
    Gen1Relay relay = new Gen1Relay(nodeTopic, 0);

    List<Boolean> stateUpdates = new ArrayList<>();
    relay.subscribeToStateUpdate(stateUpdates::add);

    nodeTopic.subTopic("/relay/0", RawMessage.toStr(), true).publish("on");

    assertThat(stateUpdates, hasItems(true));
  }

  @Test
  public void testChannelReport() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);

    Gen1Relay relay = new Gen1Relay(nodeTopic, 0);

    List<ChannelReport> channelReports = new ArrayList<>();
    relay.subscribeToChannelReport(channelReports::add);

    nodeTopic.subTopic("/voltage", RawMessage.toStr()).publish("220");
    nodeTopic.subTopic("/temperature", RawMessage.toStr()).publish("40.5");
    nodeTopic.subTopic("/relay/0/power", RawMessage.toStr()).publish("362");
    nodeTopic.subTopic("/relay/0/energy", RawMessage.toStr()).publish("123456");

    assertThat(
        channelReports,
        hasItems(
            ChannelReport.builder().voltage(220.0).build(),
            ChannelReport.builder().temperature(40.5).build(),
            ChannelReport.builder().power(362.0).build(),
            ChannelReport.builder().energy(123456.0).build()));
  }
}
