package com.arturk.fooddelivery.order.messaging.outbox;

import java.util.UUID;

public record OrderItemCreatedEventPayload(
        UUID menuItemId,
        int quantity
) {
}
