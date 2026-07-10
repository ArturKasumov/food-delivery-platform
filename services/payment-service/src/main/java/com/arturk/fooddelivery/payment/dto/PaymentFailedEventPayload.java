package com.arturk.fooddelivery.payment.dto;

import com.arturk.fooddelivery.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentFailedEventPayload(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        PaymentStatus status,
        String failureReason
) {
}
