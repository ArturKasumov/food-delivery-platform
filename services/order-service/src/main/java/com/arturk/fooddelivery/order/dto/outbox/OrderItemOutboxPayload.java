package com.arturk.fooddelivery.order.dto.outbox;

import java.util.UUID;

public record OrderItemOutboxPayload(
        UUID menuItemId,
        int quantity
) {
}
