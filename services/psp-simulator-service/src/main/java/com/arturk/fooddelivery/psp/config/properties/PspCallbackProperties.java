package com.arturk.fooddelivery.psp.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.callback")
public record PspCallbackProperties(
        @NotBlank String callbackSecret,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {
}
