package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.messaging.outbox.OrderCreatedEventPayload;
import com.arturk.fooddelivery.order.messaging.outbox.OrderItemCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventsConverter {

    public OrderCreatedEventPayload toOrderCreatedEventOutboxPayload(CustomerOrderEntity order) {
        return new OrderCreatedEventPayload(
                order.getId(),
                order.getCustomerId(),
                order.getRestaurantId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(item -> new OrderItemCreatedEventPayload(item.getMenuItemId(), item.getQuantity()))
                        .toList()
        );
    }
}
