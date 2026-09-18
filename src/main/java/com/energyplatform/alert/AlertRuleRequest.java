package com.energyplatform.alert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlertRuleRequest(
    @NotNull Long deviceId,
    @NotNull MeasurementMetric metric,
    @NotNull RuleOperator operator,
    double threshold,
    @NotBlank String alertType,
    boolean enabled) {}
