package com.energyplatform.measurement;

import com.energyplatform.common.config.KafkaTopicConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MeasurementConsumer {

  private final MeasurementService measurementService;

  public MeasurementConsumer(MeasurementService measurementService) {
    this.measurementService = measurementService;
  }

  @KafkaListener(topics = KafkaTopicConfig.MEASUREMENTS_TOPIC, groupId = "measurement-service")
  public void consume(MeasurementEvent event) {
    measurementService.save(event);
  }
}
