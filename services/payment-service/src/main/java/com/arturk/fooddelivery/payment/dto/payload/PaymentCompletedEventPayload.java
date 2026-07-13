package com.arturk.fooddelivery.payment.dto.payload;

import com.arturk.fooddelivery.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCompletedEventPayload(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        PaymentStatus status
) {
}
