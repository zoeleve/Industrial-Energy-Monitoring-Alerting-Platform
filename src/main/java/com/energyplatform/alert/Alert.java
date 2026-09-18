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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "device_id", nullable = false)
  private Device device;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rule_id", nullable = false)
  private AlertRule rule;

  @Column(name = "triggering_value", nullable = false)
  private double triggeringValue;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AlertStatus status;

  @Column(name = "triggered_at", nullable = false)
  private LocalDateTime triggeredAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;

  protected Alert() {
    // required by JPA
  }

  public Alert(Device device, AlertRule rule, double triggeringValue) {
    this.device = device;
    this.rule = rule;
    this.triggeringValue = triggeringValue;
    this.status = AlertStatus.OPEN;
  }

  @PrePersist
  void onCreate() {
    this.triggeredAt = LocalDateTime.now();
  }

  public void resolve() {
    this.status = AlertStatus.RESOLVED;
    this.resolvedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Device getDevice() {
    return device;
  }

  public AlertRule getRule() {
    return rule;
  }

  public double getTriggeringValue() {
    return triggeringValue;
  }

  public AlertStatus getStatus() {
    return status;
  }

  public LocalDateTime getTriggeredAt() {
    return triggeredAt;
  }

  public LocalDateTime getResolvedAt() {
    return resolvedAt;
  }
}
