package com.arturk.fooddelivery.order.cucumber.data;

import com.arturk.fooddelivery.order.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CustomerOrderData {

    private UUID customerId;
    private UUID restaurantId;
    private OrderStatus status;
    private List<OrderItemData> items = new ArrayList<>();
}
