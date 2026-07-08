package com.arturk.fooddelivery.order.mapper.kafka;

import com.arturk.fooddelivery.order.constants.OrderEventTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaEventMapperRegisterTest {

    @Mock
    private KafkaEventMapper orderCreatedEventMapper;

    @Test
    void returnsMapperByEventType() {
        when(orderCreatedEventMapper.getEventType()).thenReturn(OrderEventTypes.ORDER_CREATED_EVENT_TYPE);

        KafkaEventMapperRegistry registry =
                new KafkaEventMapperRegistry(List.of(orderCreatedEventMapper));

        registry.init();

        KafkaEventMapper mapper = registry.getKafkaEventMapper(OrderEventTypes.ORDER_CREATED_EVENT_TYPE);

        assertThat(mapper).isSameAs(orderCreatedEventMapper);
    }

    @Test
    void throwsWhenEventTypeIsUnsupported() {
        KafkaEventMapperRegistry registry =
                new KafkaEventMapperRegistry(List.of(orderCreatedEventMapper));

        registry.init();

        assertThatThrownBy(() -> registry.getKafkaEventMapper("UnknownEvent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported event type: UnknownEvent");
    }

}
