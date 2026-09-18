package com.energyplatform.alert;

public record AlertRuleResponse(
    Long id,
    Long deviceId,
    MeasurementMetric metric,
    RuleOperator operator,
    double threshold,
    String alertType,
    boolean enabled) {

  public static AlertRuleResponse from(AlertRule rule) {
    return new AlertRuleResponse(
        rule.getId(),
        rule.getDevice().getId(),
        rule.getMetric(),
        rule.getOperator(),
        rule.getThreshold(),
        rule.getAlertType(),
        rule.isEnabled());
  }
}
