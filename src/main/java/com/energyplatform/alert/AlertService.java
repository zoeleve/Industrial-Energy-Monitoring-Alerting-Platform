package com.energyplatform.alert;

import com.energyplatform.common.exception.ResourceNotFoundException;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceRepository;
import com.energyplatform.measurement.Measurement;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertService {

  private final AlertRuleRepository alertRuleRepository;
  private final AlertRepository alertRepository;
  private final DeviceRepository deviceRepository;
  private final AlertEvaluator alertEvaluator;

  public AlertService(
      AlertRuleRepository alertRuleRepository,
      AlertRepository alertRepository,
      DeviceRepository deviceRepository,
      AlertEvaluator alertEvaluator) {
    this.alertRuleRepository = alertRuleRepository;
    this.alertRepository = alertRepository;
    this.deviceRepository = deviceRepository;
    this.alertEvaluator = alertEvaluator;
  }

  @Transactional
  public AlertRuleResponse createRule(AlertRuleRequest request) {
    Device device =
        deviceRepository
            .findById(request.deviceId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Device not found: " + request.deviceId()));

    AlertRule rule =
        new AlertRule(
            device,
            request.metric(),
            request.operator(),
            request.threshold(),
            request.alertType(),
            request.enabled());
    return AlertRuleResponse.from(alertRuleRepository.save(rule));
  }

  @Transactional(readOnly = true)
  public Page<AlertRuleResponse> findRules(Pageable pageable) {
    return alertRuleRepository.findAll(pageable).map(AlertRuleResponse::from);
  }

  @Transactional
  public void evaluate(Measurement measurement) {
    List<AlertRule> rules =
        alertRuleRepository.findByDeviceIdAndEnabledTrue(measurement.getDevice().getId());

    for (AlertEvaluator.Trigger trigger : alertEvaluator.evaluate(measurement, rules)) {
      alertRepository.save(new Alert(measurement.getDevice(), trigger.rule(), trigger.value()));
    }
  }

  @Transactional(readOnly = true)
  public Page<AlertResponse> findAlerts(Pageable pageable) {
    return alertRepository.findAll(pageable).map(AlertResponse::from);
  }

  @Transactional
  public AlertResponse resolve(Long alertId) {
    Alert alert =
        alertRepository
            .findById(alertId)
            .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));
    alert.resolve();
    return AlertResponse.from(alert);
  }
}
