package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.messaging.outbox.OrderCreatedEventPayload;
import com.arturk.fooddelivery.order.messaging.outbox.OrderItemCreatedEventPayload;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

        CustomerOrderEntity orderEntity = new CustomerOrderEntity(customerId, restaurantId, new BigDecimal("175.25"));
        orderEntity.addItem(firstMenuItemId, 1);
        orderEntity.addItem(secondMenuItemId, 2);

        OrderCreatedEventPayload result = outboxEventsConverter.toOrderCreatedEventOutboxPayload(orderEntity);

        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderEntity.getId());
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.restaurantId()).isEqualTo(restaurantId);
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(result.totalAmount()).isEqualByComparingTo("175.25");

        assertThat(result.items()).hasSize(2);

        assertThat(result.items())
                .extracting(OrderItemCreatedEventPayload::menuItemId)
                .containsExactly(firstMenuItemId, secondMenuItemId);

        assertThat(result.items())
                .extracting(OrderItemCreatedEventPayload::quantity)
                .containsExactly(1, 2);
    }

}
