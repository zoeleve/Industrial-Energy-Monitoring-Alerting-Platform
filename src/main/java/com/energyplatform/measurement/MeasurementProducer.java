package com.energyplatform.measurement;

import com.energyplatform.common.config.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MeasurementProducer {

  private final KafkaTemplate<String, MeasurementEvent> kafkaTemplate;

  public MeasurementProducer(KafkaTemplate<String, MeasurementEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(MeasurementEvent event) {
    String key = String.valueOf(event.deviceId());
    kafkaTemplate.send(KafkaTopicConfig.MEASUREMENTS_TOPIC, key, event);
  }
}
