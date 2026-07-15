package com.arturk.fooddelivery.payment.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.checkout.worker")
public record CheckoutWorkerProperties(
        boolean enabled,
        @Min(1) int batchSize,
        @NotNull Duration fixedDelay,
        @Min(1) int maxRetryAttempts,
        @NotNull Duration retryInitialDelay,
        @NotNull Duration processingTimeout
) {
    public CheckoutWorkerProperties {
        if (retryInitialDelay != null && (retryInitialDelay.isZero() || retryInitialDelay.isNegative())) {
            throw new IllegalArgumentException("Checkout retry initial delay must be positive");
        }
    }
}
