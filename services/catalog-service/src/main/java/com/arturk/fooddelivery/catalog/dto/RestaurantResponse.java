package com.arturk.fooddelivery.catalog.dto;

import com.arturk.fooddelivery.catalog.dto.entity.Restaurant;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;

import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String address,
        RestaurantStatus status
) {

    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getStatus()
        );
    }
}
