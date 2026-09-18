package com.energyplatform.measurement;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/measurements")
@Tag(name = "Measurements")
public class MeasurementController {

  private final MeasurementProducer measurementProducer;
  private final MeasurementService measurementService;

  public MeasurementController(
      MeasurementProducer measurementProducer, MeasurementService measurementService) {
    this.measurementProducer = measurementProducer;
    this.measurementService = measurementService;
  }

  @PostMapping
  public ResponseEntity<Void> submit(@Valid @RequestBody MeasurementEvent event) {
    measurementProducer.publish(event);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @GetMapping
  public Page<MeasurementResponse> findAll(Pageable pageable) {
    return measurementService.findAll(pageable);
  }

  @GetMapping("/device/{deviceId}")
  public Page<MeasurementResponse> findByDevice(@PathVariable Long deviceId, Pageable pageable) {
    return measurementService.findByDevice(deviceId, pageable);
  }
}
