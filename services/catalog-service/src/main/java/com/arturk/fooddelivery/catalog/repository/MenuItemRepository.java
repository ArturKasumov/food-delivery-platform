package com.arturk.fooddelivery.catalog.repository;

import com.arturk.fooddelivery.catalog.domain.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, UUID> {
}
