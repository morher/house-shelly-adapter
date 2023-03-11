package net.morher.house.shelly.api;

public interface RelayStateListener {
  void onRelaysStateUpdate(boolean state);
}
