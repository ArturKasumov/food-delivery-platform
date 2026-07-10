package com.arturk.fooddelivery.order.dto;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedResponse(
        UUID id,
        BigDecimal totalAmount
) {

    public static OrderCreatedResponse from(CustomerOrderEntity order) {
        return new OrderCreatedResponse(
                order.getId(),
                order.getTotalAmount()
        );
    }
}
