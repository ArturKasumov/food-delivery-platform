package com.arturk.fooddelivery.catalog.service.validation;

import com.arturk.fooddelivery.catalog.dto.entity.MenuItemEntity;
import com.arturk.fooddelivery.catalog.dto.entity.RestaurantEntity;
import com.arturk.fooddelivery.catalog.exception.business.MenuItemNotFoundException;
import com.arturk.fooddelivery.catalog.exception.business.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogValidationService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public boolean isOrderValid(UUID restaurantId, List<UUID> menuItemIds) {
        try {
            log.info("Validating order for restaurant: {}, items: {}", restaurantId, menuItemIds);
            RestaurantEntity restaurantEntity = restaurantRepository
                    .findById(restaurantId)
                    .orElseThrow(RestaurantNotFoundException::new);

            List<UUID> missingMenuItemIds = findMissingMenuItemIds(restaurantEntity, menuItemIds);

            if (CollectionUtils.isNotEmpty(missingMenuItemIds)) {
                throw new MenuItemNotFoundException();
            }
            return true;
        } catch (RestaurantNotFoundException | MenuItemNotFoundException exception) {
            log.error("Order validation failed: {}.", exception.getMessage());
            return false;
        } catch (Exception exception) {
            log.error("Order validation failed.", exception);
            throw exception;
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
