package com.energyplatform.measurement;

import java.time.LocalDateTime;

public record MeasurementResponse(
    Long id,
    Long deviceId,
    LocalDateTime recordedAt,
    double energyConsumption,
    double power,
    double voltage) {
  public static MeasurementResponse from(Measurement measurement) {
    return new MeasurementResponse(
        measurement.getId(),
        measurement.getDevice().getId(),
        measurement.getRecordedAt(),
        measurement.getEnergyConsumption(),
        measurement.getPower(),
        measurement.getVoltage());
  }
}
