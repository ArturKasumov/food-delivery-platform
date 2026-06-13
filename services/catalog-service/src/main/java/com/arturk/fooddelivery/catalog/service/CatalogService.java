package com.arturk.fooddelivery.catalog.service;

import com.arturk.fooddelivery.catalog.dto.CreateMenuItemRequest;
import com.arturk.fooddelivery.catalog.dto.CreateRestaurantRequest;
import com.arturk.fooddelivery.catalog.dto.MenuItemResponse;
import com.arturk.fooddelivery.catalog.dto.RestaurantResponse;
import com.arturk.fooddelivery.catalog.exception.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.dto.entity.MenuItem;
import com.arturk.fooddelivery.catalog.enums.MenuItemStatus;
import com.arturk.fooddelivery.catalog.dto.entity.Restaurant;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantResponse> findActiveRestaurants() {
        log.info("Finding active restaurants");
        return restaurantRepository.findAllByStatus(RestaurantStatus.ACTIVE)
                .stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant(request.name(), request.address());
        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant created: {}", restaurant.getId());
        return RestaurantResponse.from(restaurant);
    }

    public List<MenuItemResponse> findAvailableMenuItems(UUID restaurantId) {
        log.info("Finding available menu items for restaurant {}", restaurantId);
        Restaurant restaurant = getRestaurantById(restaurantId);

        return restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> MenuItemStatus.AVAILABLE.equals(menuItem.getStatus()))
                .map(menuItem -> MenuItemResponse.from(restaurantId, menuItem))
                .toList();
    }

    public MenuItemResponse createMenuItem(UUID restaurantId, CreateMenuItemRequest request) {
        Restaurant restaurant = getRestaurantById(restaurantId);

        MenuItem menuItem = new MenuItem(request.name(), request.description(), request.price());
        restaurant.addMenuItem(menuItem);
        restaurantRepository.save(restaurant);

        return MenuItemResponse.from(restaurantId, menuItem);
    }

    private Restaurant getRestaurantById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);
    }
}
