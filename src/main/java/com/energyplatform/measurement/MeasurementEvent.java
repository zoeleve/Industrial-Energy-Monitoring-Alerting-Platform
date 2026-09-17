package com.energyplatform.measurement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record MeasurementEvent(
    @NotNull Long deviceId,
    @NotNull LocalDateTime timestamp,
    @Positive double energyConsumption,
    @Positive double power,
    @Positive double voltage) {}
