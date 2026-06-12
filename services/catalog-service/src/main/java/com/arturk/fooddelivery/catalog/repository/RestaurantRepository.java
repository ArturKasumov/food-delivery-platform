package com.arturk.fooddelivery.catalog.repository;

import com.arturk.fooddelivery.catalog.dto.entity.Restaurant;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends MongoRepository<Restaurant, UUID> {

    List<Restaurant> findAllByStatus(RestaurantStatus status);
}
