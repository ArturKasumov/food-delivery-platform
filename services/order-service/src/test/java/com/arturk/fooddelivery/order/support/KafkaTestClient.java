package com.arturk.fooddelivery.order.support;

import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.order.config.properties.KafkaTopicsProperties;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class KafkaTestClient {

    public static final long POLL_INTERVAL_MILLIS = 500L;
    public static final long RECORD_TIMEOUT_SECONDS = 5L;

    private final KafkaTopicsProperties kafkaTopicsProperties;

    public KafkaConsumer<String, Object> getKafkaConsumer() {
        return new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestContainersSupport.kafkaBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "order-service-test-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                        ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class,
                        "schema.registry.url", TestContainersSupport.schemaRegistryUrl(),
                        "specific.avro.reader", true
                )
        );
    }

    private void waitForAssignment(KafkaConsumer<String, Object> consumer) {
        long deadline = System.nanoTime()
                + Duration.ofSeconds(RECORD_TIMEOUT_SECONDS).toNanos();

        while (consumer.assignment().isEmpty()
                && System.nanoTime() < deadline) {
            consumer.poll(Duration.ofMillis(100));
        }

        assertThat(consumer.assignment()).isNotEmpty();
    }

    public ConsumerRecord<String, Object> pollKafkaEventByEventId(KafkaConsumer<String, Object> consumer,
                                                                          UUID expectedEventId,
                                                                          Duration timeout
    ) {
        long deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(POLL_INTERVAL_MILLIS));

            for (ConsumerRecord<String, Object> record : records) {
                if (record.value() instanceof OrderCreatedEvent orderCreatedEvent
                        && expectedEventId.toString().equals(orderCreatedEvent.getMetadata().getEventId())) {
                    return record;
                }
            }
        }

        throw new AssertionError(
                "Kafka event with eventId '%s' was not received within %s"
                        .formatted(expectedEventId, timeout)
        );
    }

    public void subscribeFromEnd(KafkaConsumer<String, Object> consumer, String topic) {
        consumer.subscribe(List.of(topic));
        waitForAssignment(consumer);
        consumer.seekToEnd(consumer.assignment());
        consumer.assignment().forEach(consumer::position);
    }
}

