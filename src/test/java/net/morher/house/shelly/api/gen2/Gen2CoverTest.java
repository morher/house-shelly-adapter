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

public class Gen2CoverTest {
  private HouseMqttClient mqtt = TestHouseMqttClient.loopback();

  @Test
  public void testOpenCover() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/command/cover:0", RawMessage.toStr()).subscribe(commands::add);

    Gen2Cover cover = new Gen2Cover(nodeTopic, 0);

    cover.openCover();
    assertThat(commands, hasItems("open"));
  }

  @Test
  public void testCloseCover() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/command/cover:0", RawMessage.toStr()).subscribe(commands::add);

    Gen2Cover cover = new Gen2Cover(nodeTopic, 0);

    cover.closeCover();
    assertThat(commands, hasItems("close"));
  }

  @Test
  public void testStopCover() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/command/cover:0", RawMessage.toStr()).subscribe(commands::add);

    Gen2Cover cover = new Gen2Cover(nodeTopic, 0);

    cover.stopCover();
    assertThat(commands, hasItems("stop"));
  }

  @Test
  public void testChannelReport() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);

    Gen2Cover cover = new Gen2Cover(nodeTopic, 0);

    List<ChannelReport> channelReports = new ArrayList<>();
    cover.subscribeToChannelReport(channelReports::add);

    nodeTopic
        .subTopic("/status/cover:0", RawMessage.toStr(), true)
        .publish(
            """
            {
              "id": 0,
              "apower": 2200,
              "voltage": 220.0,
              "current": 10
            }
            """);

    assertThat(
        channelReports,
        hasItems(ChannelReport.builder().voltage(220.0).current(10.0).power(2200.0).build()));
  }
}
