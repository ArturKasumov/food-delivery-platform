package com.arturk.fooddelivery.catalog.dto.entity;

import com.arturk.fooddelivery.catalog.enums.MenuItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Field(value = "id")
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private MenuItemStatus status;
    private Instant createdAt;

    public MenuItem(String name, String description, BigDecimal price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = MenuItemStatus.AVAILABLE;
        this.createdAt = Instant.now();
    }
}
