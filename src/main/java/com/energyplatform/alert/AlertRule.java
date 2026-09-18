package com.energyplatform.alert;

import com.energyplatform.device.Device;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "alert_rules")
public class AlertRule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "device_id", nullable = false)
  private Device device;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MeasurementMetric metric;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RuleOperator operator;

  @Column(nullable = false)
  private double threshold;

  @Column(name = "alert_type", nullable = false)
  private String alertType;

  @Column(nullable = false)
  private boolean enabled;

  protected AlertRule() {
    // required by JPA
  }

  public AlertRule(
      Device device,
      MeasurementMetric metric,
      RuleOperator operator,
      double threshold,
      String alertType,
      boolean enabled) {
    this.device = device;
    this.metric = metric;
    this.operator = operator;
    this.threshold = threshold;
    this.alertType = alertType;
    this.enabled = enabled;
  }

  public Long getId() {
    return id;
  }

  public Device getDevice() {
    return device;
  }

  public MeasurementMetric getMetric() {
    return metric;
  }

  public RuleOperator getOperator() {
    return operator;
  }

  public double getThreshold() {
    return threshold;
  }

  public String getAlertType() {
    return alertType;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
