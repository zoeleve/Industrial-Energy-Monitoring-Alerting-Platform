package com.energyplatform.alert;

import com.energyplatform.measurement.Measurement;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AlertEvaluator {

  public record Trigger(AlertRule rule, double value) {}

  public List<Trigger> evaluate(Measurement measurement, List<AlertRule> rules) {
    return rules.stream()
        .map(rule -> new Trigger(rule, extract(measurement, rule.getMetric())))
        .filter(trigger -> fires(trigger.value(), trigger.rule()))
        .toList();
  }

  private boolean fires(double value, AlertRule rule) {
    return switch (rule.getOperator()) {
      case GREATER_THAN -> value > rule.getThreshold();
      case LESS_THAN -> value < rule.getThreshold();
    };
  }

  private double extract(Measurement measurement, MeasurementMetric metric) {
    return switch (metric) {
      case POWER -> measurement.getPower();
      case ENERGY_CONSUMPTION -> measurement.getEnergyConsumption();
      case VOLTAGE -> measurement.getVoltage();
    };
  }
}
