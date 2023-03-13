package net.morher.house.shelly.controller.handler;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import net.morher.house.api.devicetypes.CoverDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.cover.CoverEntity;
import net.morher.house.api.entity.cover.CoverOptions;
import net.morher.house.api.entity.cover.CoverState;
import net.morher.house.api.entity.cover.CoverStateHandler;
import net.morher.house.api.schedule.DelayedTrigger;
import net.morher.house.api.schedule.HouseScheduler;
import net.morher.house.shelly.api.ChannelReport;
import net.morher.house.shelly.api.Cover;

@Slf4j
public class CoverHandler extends ChannelHandler {
  private final HouseScheduler scheduler = HouseScheduler.get();
  private final Duration motorTimeout = Duration.ofMinutes(1);
  private final double powerMin = 1.0;
  private final Cover cover;
  private final DelayedTrigger motorTimeoutTrigger;
  private final CoverStateHandler stateHandler;
  private CoverState currentState;

  public CoverHandler(Device device, Cover cover) {
    super(device, cover);
    this.cover = cover;

    CoverEntity entity = device.entity(CoverDevice.COVER, new CoverOptions());
    motorTimeoutTrigger = scheduler.delayedTrigger("Motor timeout", this::stopAtEnd);

    cover.subscribeToChannelReport(this::onChannelReport);

    stateHandler = new CoverStateHandler(entity, this::onCoverState);
    stateHandler.setDeviceInfo(null);
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
        cover.closeCover();
        break;

      case OPENING:
        cover.openCover();
        break;

      default:
        cover.stopCover();
    }

    if (isMoving()) {
      motorTimeoutTrigger.runAfter(motorTimeout);
    } else {
      motorTimeoutTrigger.cancel();
    }
  }

  private void onChannelReport(ChannelReport report) {
    if (report.getPower() != null && report.getPower() < powerMin && isMoving()) {
      log.debug("Power use below minimum ({}): {}", powerMin, report.getPower());
      scheduler.execute(this::stopAtEnd);
    }
  }

  private boolean isMoving() {
    return CoverState.OPENING.equals(currentState) || CoverState.CLOSING.equals(currentState);
  }
}
