package com.arturk.fooddelivery.catalog.service.validation;

import com.arturk.fooddelivery.catalog.dto.entity.MenuItem;
import com.arturk.fooddelivery.catalog.dto.entity.Restaurant;
import com.arturk.fooddelivery.catalog.exception.business.MenuItemNotFoundException;
import com.arturk.fooddelivery.catalog.exception.business.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogValidationService {

    private final RestaurantRepository restaurantRepository;

    public void validateOrder(UUID restaurantId, List<UUID> menuItemIds) {
        log.info("Validating order for restaurant: {}, items: {}", restaurantId, menuItemIds);
        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

        List<UUID> missingMenuItemIds = findMissingMenuItemIds(restaurant, menuItemIds);

        if (!missingMenuItemIds.isEmpty()) {
            throw new MenuItemNotFoundException();
        }
    }

    private List<UUID> findMissingMenuItemIds(Restaurant restaurant, List<UUID> menuItemIds) {
        Set<UUID> existingMenuItemIds = restaurant.getMenuItems()
                .stream()
                .map(MenuItem::getId)
                .collect(Collectors.toSet());

        return menuItemIds.stream()
                .filter(menuItemId -> !existingMenuItemIds.contains(menuItemId))
                .toList();
    }
}
