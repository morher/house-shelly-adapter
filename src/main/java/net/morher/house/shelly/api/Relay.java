package net.morher.house.shelly.api;

import net.morher.house.api.subscription.Subscription;

public interface Relay {
  void setState(boolean state);

  Subscription subscribeToStateUpdate(RelayStateListener listener);

  Subscription subscribeToChannelReport(ChannelReportListener listener);
}
