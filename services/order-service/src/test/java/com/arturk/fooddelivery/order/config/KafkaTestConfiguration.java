package com.arturk.fooddelivery.order.config;

import com.arturk.fooddelivery.order.config.properties.KafkaTopicsProperties;
import com.arturk.fooddelivery.order.support.KafkaTestClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaTestConfiguration {

    @Bean
    NewTopic orderEventsTopic(KafkaTopicsProperties kafkaTopicsProperties) {
        return TopicBuilder.name(kafkaTopicsProperties.orderEvents())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    KafkaTestClient kafkaTestClient(KafkaTopicsProperties kafkaTopicsProperties) {
        return new KafkaTestClient(kafkaTopicsProperties);
    }
}
