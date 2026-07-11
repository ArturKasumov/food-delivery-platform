package com.arturk.fooddelivery.psp.dto;

import java.util.UUID;

public record CreateCheckoutSessionResponse(
        UUID sessionId,
        String checkoutUrl
) {
}
