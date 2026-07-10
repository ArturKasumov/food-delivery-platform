package com.arturk.fooddelivery.catalog.controller;

import com.arturk.fooddelivery.catalog.dto.request.CreateMenuItemRequest;
import com.arturk.fooddelivery.catalog.dto.request.CreateRestaurantRequest;
import com.arturk.fooddelivery.catalog.dto.response.MenuItemResponse;
import com.arturk.fooddelivery.catalog.dto.response.RestaurantResponse;
import com.arturk.fooddelivery.catalog.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/restaurants")
    public List<RestaurantResponse> findRestaurants() {
        return catalogService.findActiveRestaurants();
    }

    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return catalogService.createRestaurant(request);
    }

    @GetMapping("/restaurants/{restaurantId}/menu-items")
    public List<MenuItemResponse> findMenuItems(@PathVariable UUID restaurantId) {
        return catalogService.findAvailableMenuItems(restaurantId);
    }

    @PostMapping("/restaurants/{restaurantId}/menu-items")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse createMenuItem(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateMenuItemRequest request
    ) {
        return catalogService.createMenuItem(restaurantId, request);
    }
}
