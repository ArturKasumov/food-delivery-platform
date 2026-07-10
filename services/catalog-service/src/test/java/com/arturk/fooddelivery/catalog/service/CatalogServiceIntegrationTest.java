package com.arturk.fooddelivery.catalog.service;

import com.arturk.fooddelivery.catalog.dto.CreateMenuItemRequest;
import com.arturk.fooddelivery.catalog.dto.CreateRestaurantRequest;
import com.arturk.fooddelivery.catalog.dto.MenuItemResponse;
import com.arturk.fooddelivery.catalog.dto.RestaurantResponse;
import com.arturk.fooddelivery.catalog.domain.MenuItemEntity;
import com.arturk.fooddelivery.catalog.domain.RestaurantEntity;
import com.arturk.fooddelivery.catalog.enums.MenuItemStatus;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import com.arturk.fooddelivery.catalog.exception.business.RestaurantNotFoundException;
import com.arturk.fooddelivery.catalog.repository.MenuItemRepository;
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

    @Autowired
    private MenuItemRepository menuItemRepository;

    @BeforeEach
    void cleanDatabase() {
        menuItemRepository.deleteAll();
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

        Optional<RestaurantEntity> savedRestaurant = restaurantRepository.findById(response.id());

        assertThat(savedRestaurant).isPresent();
        assertEquals(savedRestaurant.get().getId(), response.id());
    }


    @Test
    void shouldFindOnlyActiveRestaurants() {
        //given
        RestaurantEntity active = new RestaurantEntity("Pizza House", "Main street 10");
        RestaurantEntity inactive = new RestaurantEntity("Old Cafe", "Second street 5");
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
        RestaurantEntity restaurantEntity =
                restaurantRepository.save(new RestaurantEntity("Pizza House", "Main street 10"));

        CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        "Margarita",
                        "Classic pizza",
                        BigDecimal.valueOf(5.20)
                );

        //when
        MenuItemResponse response =
                catalogService.createMenuItem(restaurantEntity.getId(), request);

        //then
        assertThat(response.id()).isNotNull();
        assertThat(response.restaurantId()).isEqualTo(restaurantEntity.getId());
        assertThat(response.name()).isEqualTo("Margarita");
        assertThat(response.status()).isEqualTo(MenuItemStatus.AVAILABLE);

        assertThat(menuItemRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldFindOnlyAvailableMenuItems() {
        //given
        RestaurantEntity restaurantEntity = new RestaurantEntity("Pizza House", "Main street 10");

        MenuItemEntity availableMenuItem =
                new MenuItemEntity("Margarita", "Classic pizza", BigDecimal.valueOf(5.20));

        MenuItemEntity unavailableMenuItem =
                new MenuItemEntity("Pepperoni", "Spicy pizza", BigDecimal.valueOf(6.10));
        unavailableMenuItem.setStatus(MenuItemStatus.UNAVAILABLE);

        restaurantEntity.addMenuItem(availableMenuItem);
        restaurantEntity.addMenuItem(unavailableMenuItem);

        restaurantEntity = restaurantRepository.save(restaurantEntity);

        //when
        List<MenuItemResponse> result = catalogService.findAvailableMenuItems(restaurantEntity.getId());

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
