package com.arturk.fooddelivery.psp.dto.response;

import java.util.UUID;

public record CreateCheckoutSessionResponse(
        UUID sessionId,
        String checkoutUrl
) {
}
