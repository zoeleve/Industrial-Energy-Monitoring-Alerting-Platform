package com.energyplatform.measurement;

import com.energyplatform.common.exception.ResourceNotFoundException;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeasurementService {

  private final MeasurementRepository measurementRepository;
  private final DeviceRepository deviceRepository;

  public MeasurementService(
      MeasurementRepository measurementRepository, DeviceRepository deviceRepository) {
    this.measurementRepository = measurementRepository;
    this.deviceRepository = deviceRepository;
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
  }

  @Transactional(readOnly = true)
  public List<MeasurementResponse> findAll() {
    return measurementRepository.findAll().stream().map(MeasurementResponse::from).toList();
  }

  @Transactional(readOnly = true)
  public List<MeasurementResponse> findByDevice(Long deviceId) {
    return measurementRepository.findByDeviceId(deviceId).stream()
        .map(MeasurementResponse::from)
        .toList();
  }
}
