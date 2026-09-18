package com.energyplatform.alert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.energyplatform.common.exception.ResourceNotFoundException;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceRepository;
import com.energyplatform.device.DeviceStatus;
import com.energyplatform.measurement.Measurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

  @Mock private AlertRuleRepository alertRuleRepository;

  @Mock private AlertRepository alertRepository;

  @Mock private DeviceRepository deviceRepository;

  // Real evaluator: it's pure logic, no reason to mock it away here.
  private AlertService alertService;

  private final Device device =
      new Device("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);

  @BeforeEach
  void setUp() {
    alertService =
        new AlertService(
            alertRuleRepository, alertRepository, deviceRepository, new AlertEvaluator());
  }

  @Test
  void createRule_savesRuleForExistingDevice() {
    when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
    when(alertRuleRepository.save(any(AlertRule.class))).thenAnswer(inv -> inv.getArgument(0));
    AlertRuleRequest request =
        new AlertRuleRequest(
            1L, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);

    AlertRuleResponse response = alertService.createRule(request);

    assertThat(response.alertType()).isEqualTo("HIGH_POWER");
    assertThat(response.threshold()).isEqualTo(100);
  }

  @Test
  void createRule_throwsWhenDeviceDoesNotExist() {
    when(deviceRepository.findById(99L)).thenReturn(Optional.empty());
    AlertRuleRequest request =
        new AlertRuleRequest(
            99L, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);

    assertThatThrownBy(() -> alertService.createRule(request))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void evaluate_createsAlertWhenRuleFires() {
    AlertRule rule =
        new AlertRule(
            device, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);
    when(alertRuleRepository.findByDeviceIdAndEnabledTrue(any())).thenReturn(List.of(rule));
    Measurement measurement = new Measurement(device, LocalDateTime.now(), 14.7, 137, 230);

    alertService.evaluate(measurement);

    ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
    verify(alertRepository).save(captor.capture());
    assertThat(captor.getValue().getTriggeringValue()).isEqualTo(137);
  }

  @Test
  void evaluate_createsNoAlertWhenNoRuleFires() {
    AlertRule rule =
        new AlertRule(
            device, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 1000, "HIGH_POWER", true);
    when(alertRuleRepository.findByDeviceIdAndEnabledTrue(any())).thenReturn(List.of(rule));
    Measurement measurement = new Measurement(device, LocalDateTime.now(), 14.7, 137, 230);

    alertService.evaluate(measurement);

    verify(alertRepository, never()).save(any());
  }

  @Test
  void resolve_marksAlertResolved() {
    Alert alert =
        new Alert(
            device,
            new AlertRule(
                device,
                MeasurementMetric.POWER,
                RuleOperator.GREATER_THAN,
                100,
                "HIGH_POWER",
                true),
            137);
    when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

    AlertResponse response = alertService.resolve(1L);

    assertThat(response.status()).isEqualTo(AlertStatus.RESOLVED);
  }

  @Test
  void resolve_throwsWhenAlertDoesNotExist() {
    when(alertRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> alertService.resolve(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
