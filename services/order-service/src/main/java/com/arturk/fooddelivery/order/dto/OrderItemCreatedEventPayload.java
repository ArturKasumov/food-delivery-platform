package com.arturk.fooddelivery.order.dto;

import java.util.UUID;

public record OrderItemCreatedEventPayload(
        UUID menuItemId,
        int quantity
) {
}
