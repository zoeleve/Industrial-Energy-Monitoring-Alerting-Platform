package com.energyplatform.device;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
public class Device {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeviceStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected Device() {
    // required by JPA
  }

  public Device(String name, String type, String location, DeviceStatus status) {
    this.name = name;
    this.type = type;
    this.location = location;
    this.status = status;
  }

  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public DeviceStatus getStatus() {
    return status;
  }

  public void setStatus(DeviceStatus status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
