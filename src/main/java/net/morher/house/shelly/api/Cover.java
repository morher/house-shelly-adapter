package net.morher.house.shelly.api;

public interface Cover extends Channel {
  void openCover();

  void closeCover();

  void stopCover();
}
