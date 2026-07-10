package com.arturk.fooddelivery.catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateMenuItemRequest(
        @NotBlank
        @Size(max = 160)
        String name,

        @NotBlank
        @Size(max = 500)
        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price
) {
}
