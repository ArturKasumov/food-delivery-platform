package com.arturk.fooddelivery.order.dto;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;

import java.util.UUID;

public record OrderCreatedResponse(
        UUID id
) {

    public static OrderCreatedResponse from(CustomerOrderEntity order) {
        return new OrderCreatedResponse(
                order.getId()
        );
    }
}
