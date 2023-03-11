package net.morher.house.shelly.api;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.morher.house.api.subscription.Subscription;

@Getter
public class TestRelay implements Relay {
  private final List<Boolean> stateCommands = new ArrayList<>();
  private final List<RelayStateListener> stateUpdateListeners = new ArrayList<>();
  private final List<ChannelReportListener> channelReportListeners = new ArrayList<>();

  @Override
  public void setState(boolean state) {
    stateCommands.add(state);
  }

  @Override
  public Subscription subscribeToStateUpdate(RelayStateListener listener) {
    stateUpdateListeners.add(listener);
    return () -> stateUpdateListeners.remove(listener);
  }

  @Override
  public Subscription subscribeToChannelReport(ChannelReportListener listener) {
    channelReportListeners.add(listener);
    return () -> channelReportListeners.remove(listener);
  }
}
