package net.morher.house.shelly.api.gen2;

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

public class Gen2RelayTest {
  private HouseMqttClient mqtt = TestHouseMqttClient.loopback();

  @Test
  public void testSetState() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/command/switch:0", RawMessage.toStr()).subscribe(commands::add);

    Gen2Relay relay = new Gen2Relay(nodeTopic, 0);

    relay.setState(true);
    assertThat(commands, hasItems("on"));

    relay.setState(false);
    assertThat(commands, hasItems("on", "off"));
  }

  @Test
  public void testStateUpdate() {
    Topic<Object> nodeTopic = mqtt.topic("shellies/test-node", null);
    Gen2Relay relay = new Gen2Relay(nodeTopic, 0);

    List<Boolean> stateUpdates = new ArrayList<>();
    relay.subscribeToStateUpdate(stateUpdates::add);

    nodeTopic
        .subTopic("/status/switch:0", RawMessage.toStr(), true)
        .publish(
            """
                {
                  "id": 0,
                  "output": true,
                  "apower": 2200,
                  "voltage": 220.0,
                  "current": 10,
                  "temperature": {
                    "tC": 69.8,
                    "tF": 157.6
                  }
                }
                """);

    assertThat(stateUpdates, hasItems(true));
  }

  @Test
  public void testChannelReport() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);

    Gen2Relay relay = new Gen2Relay(nodeTopic, 0);

    List<ChannelReport> channelReports = new ArrayList<>();
    relay.subscribeToChannelReport(channelReports::add);

    nodeTopic
        .subTopic("/status/switch:0", RawMessage.toStr(), true)
        .publish(
            """
            {
              "id": 0,
              "output": true,
              "apower": 2200,
              "voltage": 220.0,
              "current": 10,
              "aenergy": {
                "total": 123456,
                "by_minute": [
                  0,
                  0,
                  0
                ]
              },
              "temperature": {
                "tC": 69.8,
                "tF": 157.6
              }
            }
            """);

    assertThat(
        channelReports,
        hasItems(
            ChannelReport.builder()
                .voltage(220.0)
                .current(10.0)
                .temperature(69.8)
                .power(2200.0)
                .energy(123456.0)
                .build()));
  }
}
