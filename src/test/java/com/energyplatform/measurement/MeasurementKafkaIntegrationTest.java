package com.energyplatform.measurement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.energyplatform.common.config.KafkaTopicConfig;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceRepository;
import com.energyplatform.device.DeviceStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

/**
 * Publishes a real message through an in-memory Kafka broker and asserts it comes out the other end
 * persisted — the only test that exercises the producer -> topic -> consumer -> DB path as a whole,
 * rather than each piece mocked in isolation.
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, topics = KafkaTopicConfig.MEASUREMENTS_TOPIC)
class MeasurementKafkaIntegrationTest {

  @Autowired private MeasurementProducer measurementProducer;

  @Autowired private MeasurementRepository measurementRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Test
  void publishedEvent_isConsumedAndPersisted() {
    Device device =
        deviceRepository.save(
            new Device("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE));
    MeasurementEvent event =
        new MeasurementEvent(device.getId(), LocalDateTime.now(), 14.7, 5.2, 230);

    measurementProducer.publish(event);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              List<Measurement> stored = measurementRepository.findByDeviceId(device.getId());
              assertThat(stored).hasSize(1);
              assertThat(stored.get(0).getPower()).isEqualTo(5.2);
            });
  }
}
