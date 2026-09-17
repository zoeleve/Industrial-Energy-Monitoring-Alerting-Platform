package com.energyplatform.measurement;

import com.energyplatform.device.Device;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "measurements")
public class Measurement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "device_id", nullable = false)
  private Device device;

  @Column(name = "recorded_at", nullable = false)
  private LocalDateTime recordedAt;

  @Column(name = "energy_consumption", nullable = false)
  private double energyConsumption;

  @Column(nullable = false)
  private double power;

  @Column(nullable = false)
  private double voltage;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected Measurement() {
    // required by JPA
  }

  public Measurement(
      Device device,
      LocalDateTime recordedAt,
      double energyConsumption,
      double power,
      double voltage) {
    this.device = device;
    this.recordedAt = recordedAt;
    this.energyConsumption = energyConsumption;
    this.power = power;
    this.voltage = voltage;
  }

  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Device getDevice() {
    return device;
  }

  public LocalDateTime getRecordedAt() {
    return recordedAt;
  }

  public double getEnergyConsumption() {
    return energyConsumption;
  }

  public double getPower() {
    return power;
  }

  public double getVoltage() {
    return voltage;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
