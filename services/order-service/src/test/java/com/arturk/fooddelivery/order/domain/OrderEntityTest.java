package com.arturk.fooddelivery.order.domain;

import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityTest {

    @Test
    void newOrderWithItem() {
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        CustomerOrderEntity order = new CustomerOrderEntity(customerId, restaurantId);
        order.addItem(menuItemId, 3);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getItems())
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getOrder()).isSameAs(order);
                    assertThat(item.getMenuItemId()).isEqualTo(menuItemId);
                    assertThat(item.getQuantity()).isEqualTo(3);
                });
    }
}
