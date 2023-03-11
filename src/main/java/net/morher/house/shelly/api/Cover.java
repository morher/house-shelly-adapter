package net.morher.house.shelly.api;

import net.morher.house.api.subscription.Subscription;

public interface Cover {
  void openCover();

  void closeCover();

  void stopCover();

  Subscription subscribeToChannelReport(ChannelReportListener listener);
}
