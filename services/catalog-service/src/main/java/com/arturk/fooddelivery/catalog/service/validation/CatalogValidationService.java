package com.arturk.fooddelivery.catalog.service.validation;

import com.arturk.fooddelivery.catalog.dto.entity.MenuItemEntity;
import com.arturk.fooddelivery.catalog.dto.entity.RestaurantEntity;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        log.info("Validating order for restaurant: {}, items: {}", restaurantId, menuItemIds);
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);

        if (restaurantEntityOptional.isEmpty()) {
            log.warn("Order validation failed: restaurant not found, restaurantId: {}.", restaurantId);
            return false;
        }

        List<UUID> missingMenuItemIds = findMissingMenuItemIds(restaurantEntityOptional.get(), menuItemIds);

        if (CollectionUtils.isNotEmpty(missingMenuItemIds)) {
            log.warn("Order validation failed: missing menu items: {}.", missingMenuItemIds);
            return false;
        }
        return true;
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
