package com.energyplatform.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String location,
        @NotNull DeviceStatus status
) {
}
