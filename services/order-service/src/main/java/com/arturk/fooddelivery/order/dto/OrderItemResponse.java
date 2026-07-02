package com.arturk.fooddelivery.order.dto;

import com.arturk.fooddelivery.order.domain.OrderItemEntity;

import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID menuItemId,
        int quantity
) {

    public static OrderItemResponse from(OrderItemEntity item) {
        return new OrderItemResponse(
                item.getId(),
                item.getMenuItemId(),
                item.getQuantity()
        );
    }
}
