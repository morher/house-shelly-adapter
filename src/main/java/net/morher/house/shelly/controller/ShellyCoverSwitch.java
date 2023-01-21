package net.morher.house.shelly.controller;

import java.io.Closeable;
import net.morher.house.api.entity.cover.CoverCommand;
import net.morher.house.api.entity.cover.CoverEntity;
import net.morher.house.api.entity.cover.CoverState;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.api.entity.switches.SwitchStateHandler;
import net.morher.house.api.subscription.Subscription;

public class ShellyCoverSwitch implements Closeable {
  private final CoverEntity coverEntity;
  private final Subscription coverStateSubscription;
  private final SwitchStateHandler switchStateHandler;

  public ShellyCoverSwitch(CoverEntity coverEntity, SwitchEntity switchEntity) {
    this.coverEntity = coverEntity;

    coverStateSubscription = coverEntity.state().subscribe(this::onCoverState);
    switchStateHandler = new SwitchStateHandler(switchEntity, this::onSwitchState);
  }

  private void onCoverState(CoverState coverState) {
    boolean isOn = CoverState.CLOSING.equals(coverState) || CoverState.CLOSED.equals(coverState);
    switchStateHandler.updateState(isOn);
  }

  private void onSwitchState(Boolean switchState) {
    if (switchState) {
      coverEntity.sendCommand(CoverCommand.CLOSE);
    } else {
      coverEntity.sendCommand(CoverCommand.OPEN);
    }
  }

  @Override
  public void close() {
    coverStateSubscription.close();
  }
}
