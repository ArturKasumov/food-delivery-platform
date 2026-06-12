package com.arturk.fooddelivery.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(
        @NotBlank
        @Size(max = 160)
        String name,

        @NotBlank
        @Size(max = 240)
        String address
) {

}
