package com.energyplatform.alert;

import static org.assertj.core.api.Assertions.assertThat;

import com.energyplatform.alert.AlertEvaluator.Trigger;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceStatus;
import com.energyplatform.measurement.Measurement;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AlertEvaluatorTest {

  private final AlertEvaluator evaluator = new AlertEvaluator();
  private final Device device =
      new Device("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);

  @Test
  void greaterThanRule_firesWhenValueExceedsThreshold() {
    Measurement measurement = new Measurement(device, LocalDateTime.now(), 14.7, 137, 230);
    AlertRule rule =
        new AlertRule(
            device, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);

    List<Trigger> fired = evaluator.evaluate(measurement, List.of(rule));

    assertThat(fired).hasSize(1);
    assertThat(fired.get(0).value()).isEqualTo(137);
  }

  @Test
  void greaterThanRule_doesNotFireWhenValueBelowThreshold() {
    Measurement measurement = new Measurement(device, LocalDateTime.now(), 14.7, 50, 230);
    AlertRule rule =
        new AlertRule(
            device, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);

    assertThat(evaluator.evaluate(measurement, List.of(rule))).isEmpty();
  }

  @Test
  void lessThanRule_firesWhenValueBelowThreshold() {
    Measurement measurement = new Measurement(device, LocalDateTime.now(), 14.7, 5.2, 180);
    AlertRule rule =
        new AlertRule(
            device, MeasurementMetric.VOLTAGE, RuleOperator.LESS_THAN, 200, "LOW_VOLTAGE", true);

    List<Trigger> fired = evaluator.evaluate(measurement, List.of(rule));

    assertThat(fired).hasSize(1);
    assertThat(fired.get(0).rule().getAlertType()).isEqualTo("LOW_VOLTAGE");
  }

  @Test
  void evaluate_onlyReturnsRulesThatActuallyFired() {
    Measurement measurement = new Measurement(device, LocalDateTime.now(), 999, 137, 230);
    AlertRule firingRule =
        new AlertRule(
            device, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);
    AlertRule quietRule =
        new AlertRule(
            device,
            MeasurementMetric.ENERGY_CONSUMPTION,
            RuleOperator.GREATER_THAN,
            5000,
            "ENERGY_SPIKE",
            true);

    List<Trigger> fired = evaluator.evaluate(measurement, List.of(firingRule, quietRule));

    assertThat(fired).extracting(t -> t.rule().getAlertType()).containsExactly("HIGH_POWER");
  }
}
