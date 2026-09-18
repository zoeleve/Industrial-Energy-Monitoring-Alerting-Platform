package com.energyplatform.device;

import com.energyplatform.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;

  public DeviceService(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  @Transactional
  public DeviceResponse create(DeviceRequest request) {
    Device device =
        new Device(request.name(), request.type(), request.location(), request.status());
    Device saved = deviceRepository.save(device);
    return DeviceResponse.from(saved);
  }

  @Transactional(readOnly = true)
  public Page<DeviceResponse> findAll(Pageable pageable) {
    return deviceRepository.findAll(pageable).map(DeviceResponse::from);
  }

  @Transactional(readOnly = true)
  public DeviceResponse findById(Long id) {
    return deviceRepository
        .findById(id)
        .map(DeviceResponse::from)
        .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + id));
  }

  @Transactional
  public DeviceResponse update(Long id, DeviceRequest request) {
    Device device =
        deviceRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + id));

    device.setName(request.name());
    device.setType(request.type());
    device.setLocation(request.location());
    device.setStatus(request.status());

    return DeviceResponse.from(device);
  }

  @Transactional
  public void delete(Long id) {
    if (!deviceRepository.existsById(id)) {
      throw new ResourceNotFoundException("Device not found: " + id);
    }
    deviceRepository.deleteById(id);
  }
}
