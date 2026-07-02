package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.dto.outbox.OrderCreatedEventOutboxPayload;
import com.arturk.fooddelivery.order.dto.outbox.OrderItemOutboxPayload;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventsConverter {

    public OrderCreatedEventOutboxPayload toOrderCreatedEventOutboxPayload(CustomerOrderEntity order) {
        return new OrderCreatedEventOutboxPayload(
                order.getId(),
                order.getCustomerId(),
                order.getRestaurantId(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemOutboxPayload(item.getMenuItemId(), item.getQuantity()))
                        .toList()
        );
    }
}
