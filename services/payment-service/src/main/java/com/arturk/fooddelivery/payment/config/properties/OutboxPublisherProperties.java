package com.arturk.fooddelivery.payment.config.properties;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.outbox.publisher")
public record OutboxPublisherProperties(
        boolean enabled,
        @Min(1) int batchSize,
        Duration fixedDelay,
        @Min(1) int maxRetryAttempts,
        Duration processingTimeout
) {
}
