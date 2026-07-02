package com.arturk.fooddelivery.order.dto.outbox;

import com.arturk.fooddelivery.order.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEventOutboxPayload(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        OrderStatus status,
        List<OrderItemOutboxPayload> items
) {
}
