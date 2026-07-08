package com.arturk.fooddelivery.order.mapper.outbox;

import com.arturk.fooddelivery.order.constants.OrderEventTypes;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventMapperRegisterTest {

    @Mock
    private OutboxOrderCreatedEventMapper orderCreatedEventMapper;

    @Test
    void returnsMapperByEventType() {
        when(orderCreatedEventMapper.getEventType()).thenReturn(OrderEventTypes.ORDER_CREATED_EVENT_TYPE);
        when(orderCreatedEventMapper.supportsEntityType()).thenReturn(CustomerOrderEntity.class);

        OutboxEventMapperRegistry registry =
                new OutboxEventMapperRegistry(List.of(orderCreatedEventMapper));

        registry.init();

        OutboxEventMapper<CustomerOrderEntity> mapper = registry.getOutboxEventMapper(OrderEventTypes.ORDER_CREATED_EVENT_TYPE, new CustomerOrderEntity());

        assertThat(mapper).isSameAs(orderCreatedEventMapper);
    }

    @Test
    void throwsWhenEventTypeIsUnsupported() {
        OutboxEventMapperRegistry registry =
                new OutboxEventMapperRegistry(List.of(orderCreatedEventMapper));

        registry.init();

        assertThatThrownBy(() -> registry.getOutboxEventMapper("UnknownEvent", new CustomerOrderEntity()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported event type: UnknownEvent");
    }

    @Test
    void throwsWhenEntityIsNull() {
        OutboxEventMapperRegistry registry =
                new OutboxEventMapperRegistry(List.of(orderCreatedEventMapper));

        registry.init();

        assertThatThrownBy(() -> registry.getOutboxEventMapper(OrderEventTypes.ORDER_CREATED_EVENT_TYPE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Outbox event source entity must not be null");
    }

    @Test
    void throwsWhenEntityTypeIsUnsupported() {
        when(orderCreatedEventMapper.getEventType()).thenReturn(OrderEventTypes.ORDER_CREATED_EVENT_TYPE);
        when(orderCreatedEventMapper.supportsEntityType()).thenReturn(CustomerOrderEntity.class);

        OutboxEventMapperRegistry registry =
                new OutboxEventMapperRegistry(List.of(orderCreatedEventMapper));

        registry.init();

        assertThatThrownBy(() -> registry.getOutboxEventMapper(OrderEventTypes.ORDER_CREATED_EVENT_TYPE, new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Outbox mapper for event type %s supports %s, but got %s"
                        .formatted(OrderEventTypes.ORDER_CREATED_EVENT_TYPE, CustomerOrderEntity.class.getName(), Object.class.getName())
                );
    }

}
