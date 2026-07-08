package com.arturk.fooddelivery.order.cucumber.data;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItemData {

    private UUID menuItemId;
    private Integer quantity;
}
