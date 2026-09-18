package com.energyplatform.measurement;

import com.energyplatform.alert.AlertService;
import com.energyplatform.common.exception.ResourceNotFoundException;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeasurementService {

  private final MeasurementRepository measurementRepository;
  private final DeviceRepository deviceRepository;
  private final AlertService alertService;

  public MeasurementService(
      MeasurementRepository measurementRepository,
      DeviceRepository deviceRepository,
      AlertService alertService) {
    this.measurementRepository = measurementRepository;
    this.deviceRepository = deviceRepository;
    this.alertService = alertService;
  }

  @Transactional
  public void save(MeasurementEvent event) {
    Device device =
        deviceRepository
            .findById(event.deviceId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Device not found: " + event.deviceId()));

    Measurement measurement =
        new Measurement(
            device, event.timestamp(), event.energyConsumption(), event.power(), event.voltage());
    measurementRepository.save(measurement);
    alertService.evaluate(measurement);
  }

  @Transactional(readOnly = true)
  public Page<MeasurementResponse> findAll(Pageable pageable) {
    return measurementRepository.findAll(pageable).map(MeasurementResponse::from);
  }

  @Transactional(readOnly = true)
  public Page<MeasurementResponse> findByDevice(Long deviceId, Pageable pageable) {
    return measurementRepository.findByDeviceId(deviceId, pageable).map(MeasurementResponse::from);
  }
}
