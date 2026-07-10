package com.arturk.fooddelivery.order.dto;

import java.math.BigDecimal;

public record CatalogOrderValidationResult(
        boolean valid,
        BigDecimal totalAmount
) {
}
