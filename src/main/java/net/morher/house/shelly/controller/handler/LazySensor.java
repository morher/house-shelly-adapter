package net.morher.house.shelly.controller.handler;

import java.util.function.Consumer;
import net.morher.house.api.entity.common.EntityOptions;
import net.morher.house.api.entity.common.StatefullEntity;

class LazySensor<S, O extends EntityOptions> implements Consumer<S> {
  private final StatefullEntity<S, O> entity;
  private O options;

  public LazySensor(StatefullEntity<S, O> entity) {
    this.entity = entity;
  }

  public LazySensor<S, O> withOptions(O options) {
    this.options = options;
    return this;
  }

  @Override
  public void accept(S value) {
    if (value != null) {
      if (options != null) {
        entity.setOptions(options);
        options = null;
      }
      entity.state().publish(value);
    }
  }
}
