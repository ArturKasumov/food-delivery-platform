package com.arturk.fooddelivery.psp.dto.request;

import com.arturk.fooddelivery.psp.enums.CheckoutDecision;

import java.time.Instant;
import java.util.UUID;

public record PspPaymentCallbackRequest(
        UUID eventId,
        UUID sessionId,
        UUID paymentId,
        UUID orderId,
        CheckoutDecision status,
        Instant occurredAt
) {
}
