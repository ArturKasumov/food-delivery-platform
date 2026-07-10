package com.arturk.fooddelivery.payment.mapper.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaEventMapperRegistry {

    private final List<KafkaEventMapper> kafkaEventMappers;
    private final Map<String, KafkaEventMapper> kafkaEventMapperMap = new HashMap<>();

    @PostConstruct
    public void init() {
        kafkaEventMappers.forEach(mapper -> kafkaEventMapperMap.put(mapper.getEventType(), mapper));
    }

    public KafkaEventMapper getKafkaEventMapper(String eventType) {
        KafkaEventMapper kafkaEventMapper = kafkaEventMapperMap.get(eventType);
        if (kafkaEventMapper == null) {
            throw new IllegalArgumentException("Unsupported event type: " + eventType);
        }
        return kafkaEventMapper;
    }

}
