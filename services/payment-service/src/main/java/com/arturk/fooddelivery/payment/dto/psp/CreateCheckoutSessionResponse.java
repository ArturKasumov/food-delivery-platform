package com.arturk.fooddelivery.payment.dto.psp;

import java.util.UUID;

public record CreateCheckoutSessionResponse(
        UUID sessionId,
        String checkoutUrl
) {
}
