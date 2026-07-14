package com.arturk.fooddelivery.psp.model;

import com.arturk.fooddelivery.psp.enums.CheckoutSessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record CheckoutSession(
        UUID id,
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        String currency,
        String checkoutUrl,
        String callbackUrl,
        Map<String, String> metadata,
        CheckoutSessionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public boolean isPending() {
        return status == CheckoutSessionStatus.CREATED;
    }

    public CheckoutSession markPaid() {
        if (!isPending()) {
            return this;
        }

        return withStatus(CheckoutSessionStatus.PAID);
    }

    public CheckoutSession markCancelled() {
        if (!isPending()) {
            return this;
        }

        return withStatus(CheckoutSessionStatus.CANCELLED);
    }

    private CheckoutSession withStatus(CheckoutSessionStatus status) {
        return new CheckoutSession(
                id,
                paymentId,
                orderId,
                amount,
                currency,
                checkoutUrl,
                callbackUrl,
                metadata,
                status,
                createdAt,
                LocalDateTime.now()
        );
    }
}
