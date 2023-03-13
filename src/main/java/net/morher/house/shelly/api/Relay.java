package net.morher.house.shelly.api;

import net.morher.house.api.subscription.Subscription;

public interface Relay extends Channel {
  void setState(boolean state);

  Subscription subscribeToStateUpdate(RelayStateListener listener);
}
