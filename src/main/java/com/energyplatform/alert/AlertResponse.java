package com.energyplatform.alert;

import java.time.LocalDateTime;

public record AlertResponse(
    Long id,
    Long deviceId,
    Long ruleId,
    String alertType,
    double triggeringValue,
    AlertStatus status,
    LocalDateTime triggeredAt,
    LocalDateTime resolvedAt) {

  public static AlertResponse from(Alert alert) {
    return new AlertResponse(
        alert.getId(),
        alert.getDevice().getId(),
        alert.getRule().getId(),
        alert.getRule().getAlertType(),
        alert.getTriggeringValue(),
        alert.getStatus(),
        alert.getTriggeredAt(),
        alert.getResolvedAt());
  }
}
