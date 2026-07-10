package com.arturk.fooddelivery.catalog.service;

import com.arturk.fooddelivery.catalog.dto.grpc.OrderItemValidationRequest;
import com.arturk.fooddelivery.catalog.domain.MenuItemEntity;
import com.arturk.fooddelivery.catalog.domain.RestaurantEntity;
import com.arturk.fooddelivery.catalog.dto.grpc.OrderValidationResult;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.util.Map;
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
    public OrderValidationResult validateOrder(UUID restaurantId, List<OrderItemValidationRequest> items) {
        List<UUID> menuItemIdsRequest = items.stream()
                .map(OrderItemValidationRequest::menuItemId)
                .toList();
        log.info("Validating order for restaurant: {}, items: {}", restaurantId, menuItemIdsRequest);

        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);

        if (restaurantEntityOptional.isEmpty()) {
            log.warn("Order validation failed: restaurant not found, restaurantId: {}.", restaurantId);
            return OrderValidationResult.invalid();
        }

        RestaurantEntity restaurant = restaurantEntityOptional.get();
        List<UUID> invalidMenuItemIds = getInvalidMenuItemIds(restaurant, menuItemIdsRequest);

        if (CollectionUtils.isNotEmpty(invalidMenuItemIds)) {
            log.warn("Order validation failed: missing menu items: {}.", invalidMenuItemIds);
            return OrderValidationResult.invalid();
        }

        return new OrderValidationResult(true, calculateTotalAmount(restaurant, items));
    }

    private List<UUID> getInvalidMenuItemIds(RestaurantEntity restaurantEntity, List<UUID> menuItemIds) {
        Set<UUID> existingMenuItemIds = restaurantEntity.getMenuItems()
                .stream()
                .map(MenuItemEntity::getId)
                .collect(Collectors.toSet());

        return menuItemIds.stream()
                .filter(menuItemId -> !existingMenuItemIds.contains(menuItemId))
                .toList();
    }

    private BigDecimal calculateTotalAmount(RestaurantEntity restaurantEntity, List<OrderItemValidationRequest> orderItems) {
        Map<UUID, BigDecimal> pricesByMenuItemId = restaurantEntity.getMenuItems()
                .stream()
                .collect(Collectors.toMap(MenuItemEntity::getId, MenuItemEntity::getPrice));

        return orderItems.stream()
                .map(orderItem -> pricesByMenuItemId.get(orderItem.menuItemId())
                        .multiply(BigDecimal.valueOf(orderItem.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
