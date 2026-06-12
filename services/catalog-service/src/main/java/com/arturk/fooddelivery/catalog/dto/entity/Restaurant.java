package com.arturk.fooddelivery.catalog.dto.entity;

import com.arturk.fooddelivery.catalog.enums.RestaurantStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "restaurants")
@Getter
@Setter
public class Restaurant {

    @Id
    private UUID id;
    private String name;
    private String address;
    private RestaurantStatus status;
    private List<MenuItem> menuItems = new ArrayList<>();
    private Instant createdAt;

    public Restaurant(String name, String address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.status = RestaurantStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }
}
