package com.arturk.fooddelivery.catalog.dto.grpc;

import java.math.BigDecimal;

public record OrderValidationResult(
        boolean valid,
        BigDecimal totalAmount
) {

    public static OrderValidationResult invalid() {
        return new OrderValidationResult(false, BigDecimal.ZERO);
    }
}
