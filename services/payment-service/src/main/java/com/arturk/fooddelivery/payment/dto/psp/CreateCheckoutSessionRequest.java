package com.arturk.fooddelivery.payment.dto.psp;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCheckoutSessionRequest(
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        String currency,
        String callbackUrl
) {
}
