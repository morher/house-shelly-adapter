package net.morher.house.shelly.api;

import net.morher.house.api.subscription.Subscription;

public interface Channel {

  Subscription subscribeToChannelReport(ChannelReportListener listener);
}