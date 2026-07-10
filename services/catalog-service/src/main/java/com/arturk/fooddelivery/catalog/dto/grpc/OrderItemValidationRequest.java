package com.arturk.fooddelivery.catalog.dto.grpc;

import java.util.UUID;

public record OrderItemValidationRequest(
        UUID menuItemId,
        int quantity
) {
}
