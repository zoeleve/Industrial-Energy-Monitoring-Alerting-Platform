package com.energyplatform.device;

import java.time.LocalDateTime;

public record DeviceResponse(
    Long id,
    String name,
    String type,
    String location,
    DeviceStatus status,
    LocalDateTime createdAt) {
  public static DeviceResponse from(Device device) {
    return new DeviceResponse(
        device.getId(),
        device.getName(),
        device.getType(),
        device.getLocation(),
        device.getStatus(),
        device.getCreatedAt());
  }
}
