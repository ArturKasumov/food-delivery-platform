package com.arturk.fooddelivery.order.mapper.outbox;

import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OutboxEventMapperRegistry {

    private final List<OutboxEventMapper<?>> outboxEventMappers;
    private final Map<String, OutboxEventMapper<?>> outboxEventMapperMap = new HashMap<>();

    @PostConstruct
    public void init() {
        outboxEventMappers.forEach(mapper -> outboxEventMapperMap.put(mapper.getEventType(), mapper));
    }

    public <T> OutboxEventEntity mapToOutboxEvent(String eventType, T entity) {
        return getOutboxEventMapper(eventType, entity).mapToOutboxEvent(entity);
    }

    @SuppressWarnings("unchecked")
    public <T> OutboxEventMapper<T> getOutboxEventMapper(String eventType, T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Outbox event source entity must not be null");
        }

        OutboxEventMapper<?> mapper = outboxEventMapperMap.get(eventType);

        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported event type: " + eventType);
        }

        if (!mapper.supportsEntityType().isInstance(entity)) {
            throw new IllegalArgumentException(
                    "Outbox mapper for event type %s supports %s, but got %s".formatted(
                            eventType,
                            mapper.supportsEntityType().getName(),
                            entity.getClass().getName()
                    )
            );
        }

        return (OutboxEventMapper<T>) mapper;
    }
}
