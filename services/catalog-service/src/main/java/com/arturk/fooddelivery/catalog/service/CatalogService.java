package com.arturk.fooddelivery.catalog.service;

import com.arturk.fooddelivery.catalog.dto.CreateMenuItemRequest;
import com.arturk.fooddelivery.catalog.dto.CreateRestaurantRequest;
import com.arturk.fooddelivery.catalog.dto.MenuItemResponse;
import com.arturk.fooddelivery.catalog.dto.RestaurantResponse;
import com.arturk.fooddelivery.catalog.dto.entity.RestaurantEntity;
import com.arturk.fooddelivery.catalog.exception.business.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.dto.entity.MenuItemEntity;
import com.arturk.fooddelivery.catalog.enums.MenuItemStatus;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<RestaurantResponse> findActiveRestaurants() {
        log.info("Finding active restaurants");
        return restaurantRepository.findAllByStatus(RestaurantStatus.ACTIVE)
                .stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        RestaurantEntity restaurantEntity = new RestaurantEntity(request.name(), request.address());
        restaurantEntity = restaurantRepository.save(restaurantEntity);

        log.info("Restaurant created: {}", restaurantEntity.getId());
        return RestaurantResponse.from(restaurantEntity);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> findAvailableMenuItems(UUID restaurantId) {
        log.info("Finding available menu items for restaurant {}", restaurantId);
        RestaurantEntity restaurantEntity = getRestaurantById(restaurantId);

        return restaurantEntity.getMenuItems()
                .stream()
                .filter(menuItem -> MenuItemStatus.AVAILABLE.equals(menuItem.getStatus()))
                .map(menuItem -> MenuItemResponse.from(restaurantId, menuItem))
                .toList();
    }

    @Transactional
    public MenuItemResponse createMenuItem(UUID restaurantId, CreateMenuItemRequest request) {
        RestaurantEntity restaurantEntity = getRestaurantById(restaurantId);

        MenuItemEntity menuItem = new MenuItemEntity(request.name(), request.description(), request.price());
        restaurantEntity.addMenuItem(menuItem);
        restaurantRepository.save(restaurantEntity);

        return MenuItemResponse.from(restaurantId, menuItem);
    }

    private RestaurantEntity getRestaurantById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);
    }
}
