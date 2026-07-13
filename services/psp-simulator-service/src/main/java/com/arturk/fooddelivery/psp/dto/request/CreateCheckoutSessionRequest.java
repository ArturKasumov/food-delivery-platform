package com.arturk.fooddelivery.psp.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCheckoutSessionRequest(
        @NotNull
        UUID paymentId,

        @NotNull
        UUID orderId,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal amount,

        @NotBlank
        @Size(max = 3)
        String currency,

        @NotBlank
        String callbackUrl
) {
}
