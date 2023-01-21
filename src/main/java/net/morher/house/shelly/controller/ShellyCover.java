package net.morher.house.shelly.controller;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import net.morher.house.api.entity.cover.CoverEntity;
import net.morher.house.api.entity.cover.CoverState;
import net.morher.house.api.entity.cover.CoverStateHandler;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.schedule.DelayedTrigger;
import net.morher.house.api.schedule.HouseScheduler;

@Slf4j
public class ShellyCover {
  private final ScheduledExecutorService scheduler;
  private final Duration motorTimeout = Duration.ofMinutes(1);
  private final double powerMin = 1.0;
  private final DelayedTrigger motorTimeoutTrigger;
  private final ShellyCoverTopics topics;
  private final CoverStateHandler stateHandler;
  private CoverState currentState;

  public ShellyCover(
      HouseScheduler scheduler, HouseMqttClient mqtt, String nodeName, CoverEntity entity) {

    topics = new ShellyCoverTopics(mqtt, nodeName, this::onPowerUpdate);

    stateHandler = new CoverStateHandler(entity, this::onCoverState);
    stateHandler.setDeviceInfo(null);

    this.scheduler = scheduler;
    motorTimeoutTrigger = scheduler.delayedTrigger("Motor timeout", this::stopAtEnd);
  }

  private void stopAtEnd() {
    switch (currentState) {
      case CLOSED:
      case CLOSING:
        onCoverState(CoverState.CLOSED);
        break;

      case OPEN:
      case OPENING:
        onCoverState(CoverState.OPEN);
        break;

      default:
        onCoverState(CoverState.STOPPED);
    }
    stateHandler.updateState(currentState);
  }

  public void onCoverState(CoverState state) {
    this.currentState = state;
    scheduler.execute(this::updateShellyDevice);
  }

  private void updateShellyDevice() {
    switch (currentState) {
      case CLOSING:
        topics.command(ShellyCoverState.CLOSE);
        break;

      case OPENING:
        topics.command(ShellyCoverState.OPEN);
        break;

      default:
        topics.command(ShellyCoverState.STOP);
    }

    if (isMoving()) {
      motorTimeoutTrigger.runAfter(motorTimeout);
    } else {
      motorTimeoutTrigger.cancel();
    }
  }

  public void onPowerUpdate(Double data) {
    if (data < powerMin && isMoving()) {
      log.debug("Power use below minimum ({}): {}", powerMin, data);
      scheduler.execute(this::stopAtEnd);
    }
  }

  private boolean isMoving() {
    return CoverState.OPENING.equals(currentState) || CoverState.CLOSING.equals(currentState);
  }

  public static enum ShellyCoverState {
    OPEN,
    STOP,
    CLOSE;
  }
}
