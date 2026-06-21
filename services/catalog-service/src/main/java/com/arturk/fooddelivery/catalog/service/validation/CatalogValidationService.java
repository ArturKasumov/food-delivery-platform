package com.arturk.fooddelivery.catalog.service.validation;

import com.arturk.fooddelivery.catalog.dto.entity.MenuItemEntity;
import com.arturk.fooddelivery.catalog.dto.entity.RestaurantEntity;
import com.arturk.fooddelivery.catalog.exception.business.MenuItemNotFoundException;
import com.arturk.fooddelivery.catalog.exception.business.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogValidationService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public void validateOrder(UUID restaurantId, List<UUID> menuItemIds) {
        log.info("Validating order for restaurant: {}, items: {}", restaurantId, menuItemIds);
        RestaurantEntity restaurantEntity = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

        List<UUID> missingMenuItemIds = findMissingMenuItemIds(restaurantEntity, menuItemIds);

        if (!missingMenuItemIds.isEmpty()) {
            throw new MenuItemNotFoundException();
        }
    }

    private List<UUID> findMissingMenuItemIds(RestaurantEntity restaurantEntity, List<UUID> menuItemIds) {
        Set<UUID> existingMenuItemIds = restaurantEntity.getMenuItems()
                .stream()
                .map(MenuItemEntity::getId)
                .collect(Collectors.toSet());

        return menuItemIds.stream()
                .filter(menuItemId -> !existingMenuItemIds.contains(menuItemId))
                .toList();
    }
}
