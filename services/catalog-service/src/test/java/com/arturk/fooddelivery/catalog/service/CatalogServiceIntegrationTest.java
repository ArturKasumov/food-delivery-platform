package com.arturk.fooddelivery.catalog.service;

import com.arturk.fooddelivery.catalog.dto.CreateMenuItemRequest;
import com.arturk.fooddelivery.catalog.dto.CreateRestaurantRequest;
import com.arturk.fooddelivery.catalog.dto.MenuItemResponse;
import com.arturk.fooddelivery.catalog.dto.RestaurantResponse;
import com.arturk.fooddelivery.catalog.dto.entity.MenuItem;
import com.arturk.fooddelivery.catalog.dto.entity.Restaurant;
import com.arturk.fooddelivery.catalog.enums.MenuItemStatus;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import com.arturk.fooddelivery.catalog.exception.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void cleanDatabase() {
        restaurantRepository.deleteAll();
    }

    @Test
    void shouldCreateRestaurant() {
        //given
        CreateRestaurantRequest request =
                new CreateRestaurantRequest("Pizza House", "Main street 10");

        //when
        RestaurantResponse response = catalogService.createRestaurant(request);

        //then
        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("Pizza House");
        assertThat(response.address()).isEqualTo("Main street 10");
        assertThat(response.status()).isEqualTo(RestaurantStatus.ACTIVE);

        Optional<Restaurant> savedRestaurant = restaurantRepository.findById(response.id());

        assertThat(savedRestaurant).isPresent();
        assertEquals(savedRestaurant.get().getId(), response.id());
    }


    @Test
    void shouldFindOnlyActiveRestaurants() {
        //given
        Restaurant active = new Restaurant("Pizza House", "Main street 10");
        Restaurant inactive = new Restaurant("Old Cafe", "Second street 5");
        inactive.setStatus(RestaurantStatus.INACTIVE);

        restaurantRepository.saveAll(List.of(active, inactive));

        //when
        List<RestaurantResponse> result = catalogService.findActiveRestaurants();

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Pizza House");
        assertThat(result.getFirst().status()).isEqualTo(RestaurantStatus.ACTIVE);
    }

    @Test
    void shouldCreateMenuItem() {
        // given
        Restaurant restaurant =
                restaurantRepository.save(new Restaurant("Pizza House", "Main street 10"));

        CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        "Margarita",
                        "Classic pizza",
                        BigDecimal.valueOf(5.20)
                );

        //when
        MenuItemResponse response =
                catalogService.createMenuItem(restaurant.getId(), request);

        //then
        assertThat(response.id()).isNotNull();
        assertThat(response.restaurantId()).isEqualTo(restaurant.getId());
        assertThat(response.name()).isEqualTo("Margarita");
        assertThat(response.status()).isEqualTo(MenuItemStatus.AVAILABLE);

        Optional<Restaurant> updatedRestaurant = restaurantRepository.findById(restaurant.getId());

        assertThat(updatedRestaurant).isPresent();
        assertThat(updatedRestaurant.get().getMenuItems()).hasSize(1);
    }

    @Test
    void shouldFindOnlyAvailableMenuItems() {
        //given
        Restaurant restaurant = new Restaurant("Pizza House", "Main street 10");

        MenuItem availableMenuItem =
                new MenuItem("Margarita", "Classic pizza", BigDecimal.valueOf(5.20));

        MenuItem unavailableMenuItem =
                new MenuItem("Pepperoni", "Spicy pizza", BigDecimal.valueOf(6.10));
        unavailableMenuItem.setStatus(MenuItemStatus.UNAVAILABLE);

        restaurant.addMenuItem(availableMenuItem);
        restaurant.addMenuItem(unavailableMenuItem);

        restaurant = restaurantRepository.save(restaurant);

        //when
        List<MenuItemResponse> result = catalogService.findAvailableMenuItems(restaurant.getId());

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Margarita");
        assertThat(result.getFirst().status()).isEqualTo(MenuItemStatus.AVAILABLE);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNotFound() {
        UUID restaurantId = UUID.randomUUID();

        assertThatThrownBy(
                () -> catalogService.findAvailableMenuItems(restaurantId)
        ).isInstanceOf(RestaurantNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenCreatingMenuItemForUnknownRestaurant() {
        UUID restaurantId = UUID.randomUUID();

        CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        "Margarita",
                        "Classic pizza",
                        BigDecimal.valueOf(4.50)
                );

        assertThatThrownBy(
                () -> catalogService.createMenuItem(restaurantId, request)
        ).isInstanceOf(RestaurantNotFoundException.class);
    }
}
