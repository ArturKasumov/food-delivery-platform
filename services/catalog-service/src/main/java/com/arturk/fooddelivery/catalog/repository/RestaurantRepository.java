package com.arturk.fooddelivery.catalog.repository;

import com.arturk.fooddelivery.catalog.domain.RestaurantEntity;
import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

    List<RestaurantEntity> findAllByStatus(RestaurantStatus status);
}
