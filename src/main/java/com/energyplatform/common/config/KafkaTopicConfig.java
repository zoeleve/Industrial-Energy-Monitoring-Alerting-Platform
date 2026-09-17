package com.energyplatform.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  public static final String MEASUREMENTS_TOPIC = "measurements";

  @Bean
  public NewTopic measurementsTopic() {
    return TopicBuilder.name(MEASUREMENTS_TOPIC).partitions(3).replicas(1).build();
  }
}
