package com.arturk.fooddelivery.payment.dto.psp;

import com.arturk.fooddelivery.payment.enums.PspCallbackStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PspPaymentCallbackRequest(
        UUID sessionId,
        UUID paymentId,
        UUID orderId,
        PspCallbackStatus status,
        Instant occurredAt,
        Map<String, String> metadata
) {
}
