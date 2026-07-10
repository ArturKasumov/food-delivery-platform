package com.arturk.fooddelivery.order.messaging.outbox;

import com.arturk.fooddelivery.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEventPayload(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemCreatedEventPayload> items
) {
}
