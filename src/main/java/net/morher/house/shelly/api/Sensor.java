package net.morher.house.shelly.api;

import net.morher.house.api.subscription.Subscription;

public interface Sensor {
  Subscription subscribeToSensorReport(SensorReportListener listener);
}
