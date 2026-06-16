package com.arturk.fooddelivery.order.dto;

import com.arturk.fooddelivery.order.domain.OrderItem;

import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID menuItemId,
        int quantity
) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getMenuItemId(),
                item.getQuantity()
        );
    }
}
