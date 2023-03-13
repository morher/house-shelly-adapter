package net.morher.house.shelly.controller.handler;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;
import net.morher.house.api.devicetypes.ClimateAndWeatherSensorDevice;
import net.morher.house.api.devicetypes.RoomSensorDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.sensor.BinarySensorOptions;
import net.morher.house.api.entity.sensor.BinarySensorType;
import net.morher.house.api.entity.sensor.SensorOptions;
import net.morher.house.api.entity.sensor.SensorType;
import net.morher.house.shelly.api.Sensor;
import net.morher.house.shelly.api.SensorReport;
import net.morher.house.shelly.config.ShellyConfig.ShellySensorConfig;

public class SensorHandler implements Closeable {

  private final Consumer<Boolean> openingSensor;
  private final Consumer<Double> temperatureSensor;
  private final Consumer<Double> illuminanceSensor;
  private final Consumer<Double> tiltSensor;
  private final Consumer<Boolean> vibrationSensor;
  private final Consumer<Boolean> motionSensor;
  private final Consumer<Boolean> smokeSensor;
  private final Consumer<Boolean> moistureSensor;

  public SensorHandler(Device device, Sensor sensor, ShellySensorConfig sensorConfig) {
    BinarySensorType openingType = BinarySensorType.OPENING;
    if (sensorConfig.getOpening() != null) {
      openingType = sensorConfig.getOpening().getSensorType();
    }

    openingSensor =
        new LazySensor<>(device.entity(RoomSensorDevice.OPENING))
            .withOptions(new BinarySensorOptions(openingType));

    temperatureSensor =
        new LazySensor<>(device.entity(ClimateAndWeatherSensorDevice.TEMPERATURE))
            .withOptions(new SensorOptions(SensorType.TEMPERATURE_C));

    illuminanceSensor =
        new LazySensor<>(device.entity(ClimateAndWeatherSensorDevice.ILLUMINANCE))
            .withOptions(new SensorOptions(SensorType.ILLUMINANCE_LX));

    tiltSensor =
        new LazySensor<>(device.entity(RoomSensorDevice.TILT))
            .withOptions(new SensorOptions(SensorType.ANGLE));

    vibrationSensor =
        new LazySensor<>(device.entity(RoomSensorDevice.VIBRATION))
            .withOptions(new BinarySensorOptions(BinarySensorType.VIBRATION));

    motionSensor =
        new LazySensor<>(device.entity(RoomSensorDevice.MOTION))
            .withOptions(new BinarySensorOptions(BinarySensorType.MOTION));

    smokeSensor =
        new LazySensor<>(device.entity(RoomSensorDevice.SMOKE))
            .withOptions(new BinarySensorOptions(BinarySensorType.MOTION));

    moistureSensor =
        new LazySensor<>(device.entity(RoomSensorDevice.MOISTURE))
            .withOptions(new BinarySensorOptions(BinarySensorType.MOTION));

    sensor.subscribeToSensorReport(this::onSensorReport);
  }

  private void onSensorReport(SensorReport report) {
    temperatureSensor.accept(report.getTemperature());
    illuminanceSensor.accept(report.getIlluminance());
    tiltSensor.accept(report.getTilt());
    openingSensor.accept(report.getOpening());
    vibrationSensor.accept(report.getVibrationDetected());
    motionSensor.accept(report.getMotionDetected());
    smokeSensor.accept(report.getSmokeDetected());
    moistureSensor.accept(report.getMoistureDetected());
  }

  @Override
  public void close() throws IOException {}
}
