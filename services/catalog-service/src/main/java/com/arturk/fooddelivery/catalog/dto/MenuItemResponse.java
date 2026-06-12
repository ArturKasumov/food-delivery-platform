package com.arturk.fooddelivery.catalog.dto;

import com.arturk.fooddelivery.catalog.dto.entity.MenuItem;
import com.arturk.fooddelivery.catalog.enums.MenuItemStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID restaurantId,
        String name,
        String description,
        BigDecimal price,
        MenuItemStatus status
) {

    public static MenuItemResponse from(UUID restaurantId, MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                restaurantId,
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getStatus()
        );
    }
}
