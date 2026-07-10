package com.arturk.fooddelivery.catalog.controller;

import com.arturk.fooddelivery.catalog.dto.response.RestaurantResponse;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import com.arturk.fooddelivery.catalog.service.CatalogService;
import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @SneakyThrows
    @Test
    void shouldCreateRestaurant() {
        when(catalogService.createRestaurant(any()))
                .thenReturn(new RestaurantResponse(UUID.randomUUID(), "Pizza House", "Main street 10", RestaurantStatus.ACTIVE));

        String request = """
                {
                  "name": "Pizza House",
                  "address": "Main street 10"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/catalog/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Pizza House"))
                .andExpect(jsonPath("$.address").value("Main street 10"))
                .andExpect(jsonPath("$.status").value(RestaurantStatus.ACTIVE.name()));

    }

    @SneakyThrows
    @Test
    void shouldFindActiveRestaurants() {
        UUID id = UUID.randomUUID();
        RestaurantResponse restaurantResponse = new RestaurantResponse(id, "Pizza House", "Main street 10", RestaurantStatus.ACTIVE);

        when(catalogService.findActiveRestaurants())
                .thenReturn(List.of(restaurantResponse));

        mockMvc.perform(
                        get("/api/v1/catalog/restaurants")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Pizza House"))
                .andExpect(jsonPath("$[0].address").value("Main street 10"))
                .andExpect(jsonPath("$[0].status").value(RestaurantStatus.ACTIVE.name()));

    }

    @SneakyThrows
    @Test
    void shouldReturnBadRequestWhenRestaurantNameIsBlank() {
        String request = """
                {
                  "name": "",
                  "address": "Main street 10"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/catalog/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verify(catalogService, never()).createRestaurant(any());
    }
}
