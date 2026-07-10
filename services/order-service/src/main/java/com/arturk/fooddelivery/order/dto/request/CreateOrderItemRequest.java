package com.arturk.fooddelivery.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderItemRequest(
        @NotNull
        UUID menuItemId,

        @Min(1)
        int quantity
) {
}
