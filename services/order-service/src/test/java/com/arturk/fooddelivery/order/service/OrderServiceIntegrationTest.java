package com.arturk.fooddelivery.order.service;

import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.dto.CreateOrderItemRequest;
import com.arturk.fooddelivery.order.dto.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.OrderResponse;
import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerOrderRepository orderRepository;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() {
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                customerId,
                restaurantId,
                List.of(new CreateOrderItemRequest(menuItemId, 1))
        ));

        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customerId);
        assertThat(order.restaurantId()).isEqualTo(restaurantId);
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.items())
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.id()).isNotNull();
                    assertThat(item.menuItemId()).isEqualTo(menuItemId);
                    assertThat(item.quantity()).isEqualTo(1);
                });
    }

    @Test
    void shouldFindCustomerOrdersNewestFirst() throws InterruptedException {
        UUID customerId = UUID.randomUUID();

        OrderResponse first = orderService.createOrder(new CreateOrderRequest(
                customerId,
                UUID.randomUUID(),
                List.of(new CreateOrderItemRequest(UUID.randomUUID(), 1))
        ));

        OrderResponse second = orderService.createOrder(new CreateOrderRequest(
                customerId,
                UUID.randomUUID(),
                List.of(new CreateOrderItemRequest(UUID.randomUUID(), 1))
        ));

        List<OrderResponse> customerOrders = orderService.getCustomerOrders(customerId, Pageable.unpaged());

        assertThat(customerOrders)
                .extracting(OrderResponse::id)
                .containsExactly(second.id(), first.id());
    }
}
