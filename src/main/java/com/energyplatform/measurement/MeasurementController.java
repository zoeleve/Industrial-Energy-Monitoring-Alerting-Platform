package com.energyplatform.measurement;

import jakarta.validation.Valid;
import java.util.List;
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
  public List<MeasurementResponse> findAll() {
    return measurementService.findAll();
  }

  @GetMapping("/device/{deviceId}")
  public List<MeasurementResponse> findByDevice(@PathVariable Long deviceId) {
    return measurementService.findByDevice(deviceId);
  }
}
