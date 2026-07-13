package com.arturk.fooddelivery.payment.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.psp")
public record PspProperties(
        @NotBlank String baseUrl,
        @NotBlank String callbackUrl,
        @NotBlank String callbackSecret,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout,
        @NotNull Duration callbackTimestampTolerance
) {
}
