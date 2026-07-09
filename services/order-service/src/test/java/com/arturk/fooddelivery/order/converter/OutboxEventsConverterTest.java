package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.messaging.outbox.OrderCreatedEventPayload;
import com.arturk.fooddelivery.order.messaging.outbox.OrderItemCreatedEventPayload;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventsConverterTest {

    private final OutboxEventsConverter outboxEventsConverter = new OutboxEventsConverter();

    @Test
    void shouldConvertCustomerOrderToOrderCreatedEventPayload() {
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID firstMenuItemId = UUID.randomUUID();
        UUID secondMenuItemId = UUID.randomUUID();

        CustomerOrderEntity orderEntity = new CustomerOrderEntity(customerId, restaurantId);
        orderEntity.addItem(firstMenuItemId, 1);
        orderEntity.addItem(secondMenuItemId, 2);

        OrderCreatedEventPayload result = outboxEventsConverter.toOrderCreatedEventOutboxPayload(orderEntity);

        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderEntity.getId());
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.restaurantId()).isEqualTo(restaurantId);
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);

        assertThat(result.items()).hasSize(2);

        assertThat(result.items())
                .extracting(OrderItemCreatedEventPayload::menuItemId)
                .containsExactly(firstMenuItemId, secondMenuItemId);

        assertThat(result.items())
                .extracting(OrderItemCreatedEventPayload::quantity)
                .containsExactly(1, 2);
    }

}
