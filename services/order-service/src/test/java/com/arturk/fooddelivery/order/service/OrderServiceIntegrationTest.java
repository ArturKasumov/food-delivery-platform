package com.arturk.fooddelivery.order.service;

import com.arturk.fooddelivery.order.AbstractIntegrationTest;
import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.dto.CreateOrderItemRequest;
import com.arturk.fooddelivery.order.dto.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.OrderCreatedResponse;
import com.arturk.fooddelivery.order.dto.OrderResponse;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerOrderRepository orderRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void cleanDatabase() {
        outboxEventRepository.deleteAll();
        orderRepository.deleteAll();

        when(catalogValidationClient.isOrderValid(any(UUID.class), anyList())).thenReturn(true);

        MDC.put(CorrelationIdConstants.MDC_KEY, UUID.randomUUID().toString());
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void createOrderSuccessfully_shouldSaveOrderAndOrderCreatedEvent() {
        //given
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        //when
        OrderCreatedResponse order = orderService.createOrder(new CreateOrderRequest(
                customerId,
                restaurantId,
                List.of(new CreateOrderItemRequest(menuItemId, 1))
        ));

        //then
        CustomerOrderEntity savedOrder = orderRepository.findOrderWithItemsById(order.id()).orElseThrow();

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCustomerId()).isEqualTo(customerId);
        assertThat(savedOrder.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(savedOrder.getItems())
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getMenuItemId()).isEqualTo(menuItemId);
                    assertThat(item.getQuantity()).isEqualTo(1);
                });

        assertThat(outboxEventRepository.findAll())
                .filteredOn(event -> event.getAggregateId().equals(order.id()))
                .filteredOn(event -> event.getEventType().equals(ORDER_CREATED_EVENT_TYPE))
                .singleElement()
                .satisfies(event -> {
                    assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
                    assertThat(event.getCorrelationId()).isNotBlank();
                });
    }

    @Test
    void shouldFindCustomerOrdersNewestFirst() {
        //given
        UUID customerId = UUID.randomUUID();
        CustomerOrderEntity firstOrder = orderRepository.save(new CustomerOrderEntity(customerId, UUID.randomUUID()));
        CustomerOrderEntity secondOrder = orderRepository.save(new CustomerOrderEntity(customerId, UUID.randomUUID()));

        //when
        List<OrderResponse> customerOrders = orderService.getCustomerOrders(customerId, Pageable.unpaged());

        //then
        assertThat(customerOrders)
                .extracting(OrderResponse::id)
                .containsExactly(secondOrder.getId(), firstOrder.getId());
    }
}
