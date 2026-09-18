package com.energyplatform.device;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Devices")
public class DeviceController {

  private final DeviceService deviceService;

  public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @PostMapping
  public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequest request) {
    DeviceResponse response = deviceService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public Page<DeviceResponse> findAll(Pageable pageable) {
    return deviceService.findAll(pageable);
  }

  @GetMapping("/{id}")
  public DeviceResponse findById(@PathVariable Long id) {
    return deviceService.findById(id);
  }

  @PutMapping("/{id}")
  public DeviceResponse update(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
    return deviceService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    deviceService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
