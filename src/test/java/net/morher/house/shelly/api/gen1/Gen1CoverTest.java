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

public class Gen1CoverTest {
  private HouseMqttClient mqtt = TestHouseMqttClient.loopback();

  @Test
  public void testOpenCover() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/roller/0/command", RawMessage.toStr()).subscribe(commands::add);

    Gen1Cover cover = new Gen1Cover(nodeTopic, 0);

    cover.openCover();
    assertThat(commands, hasItems("open"));
  }

  @Test
  public void testCloseCover() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/roller/0/command", RawMessage.toStr()).subscribe(commands::add);

    Gen1Cover cover = new Gen1Cover(nodeTopic, 0);

    cover.closeCover();
    assertThat(commands, hasItems("close"));
  }

  @Test
  public void testStopCover() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);
    List<String> commands = new ArrayList<>();
    nodeTopic.subTopic("/roller/0/command", RawMessage.toStr()).subscribe(commands::add);

    Gen1Cover cover = new Gen1Cover(nodeTopic, 0);

    cover.stopCover();
    assertThat(commands, hasItems("stop"));
  }

  @Test
  public void testChannelReport() {
    Topic<?> nodeTopic = mqtt.topic("shellies/test-node", null);

    Gen1Cover cover = new Gen1Cover(nodeTopic, 0);

    List<ChannelReport> channelReports = new ArrayList<>();
    cover.subscribeToChannelReport(channelReports::add);

    nodeTopic.subTopic("/voltage", RawMessage.toStr()).publish("220");
    nodeTopic.subTopic("/temperature", RawMessage.toStr()).publish("40.5");
    nodeTopic.subTopic("/roller/0/power", RawMessage.toStr()).publish("362");
    nodeTopic.subTopic("/roller/0/energy", RawMessage.toStr()).publish("123456");

    assertThat(
        channelReports,
        hasItems(
            ChannelReport.builder().voltage(220.0).build(),
            ChannelReport.builder().temperature(40.5).build(),
            ChannelReport.builder().power(362.0).build(),
            ChannelReport.builder().energy(123456.0).build()));
  }
}
