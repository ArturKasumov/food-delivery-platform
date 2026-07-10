package com.arturk.fooddelivery.catalog.dto;

import com.arturk.fooddelivery.catalog.domain.RestaurantEntity;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;

import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String address,
        RestaurantStatus status
) {

    public static RestaurantResponse from(RestaurantEntity restaurantEntity) {
        return new RestaurantResponse(
                restaurantEntity.getId(),
                restaurantEntity.getName(),
                restaurantEntity.getAddress(),
                restaurantEntity.getStatus()
        );
    }
}
