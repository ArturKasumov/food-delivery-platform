package com.arturk.fooddelivery.order.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.outbox.publisher")
public record OutboxPublisherProperties(
        boolean enabled,
        @Min(1) int batchSize,
        @NotNull Duration fixedDelay,
        @Min(1) int maxRetryAttempts,
        @NotNull Duration processingTimeout
) {
}
