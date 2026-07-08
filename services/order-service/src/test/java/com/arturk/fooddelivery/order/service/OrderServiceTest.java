package com.arturk.fooddelivery.order.service;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.dto.CreateOrderItemRequest;
import com.arturk.fooddelivery.order.dto.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.OrderResponse;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.exception.business.CatalogValidationException;
import com.arturk.fooddelivery.order.exception.business.OrderNotFoundException;
import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
import com.arturk.fooddelivery.order.service.grpc.client.CatalogValidationClient;
import com.arturk.fooddelivery.order.service.outbox.OutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private CatalogValidationClient catalogValidationClient;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderSuccessfully() {
        //given
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                restaurantId,
                List.of(new CreateOrderItemRequest(menuItemId, 2))
        );

        when(catalogValidationClient.isOrderValid(restaurantId, List.of(menuItemId))).thenReturn(true);
        when(orderRepository.save(any(CustomerOrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        OrderResponse response = orderService.createOrder(request);

        //then
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.restaurantId()).isEqualTo(restaurantId);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(response.items())
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.menuItemId()).isEqualTo(menuItemId);
                    assertThat(item.quantity()).isEqualTo(2);
                });

        ArgumentCaptor<CustomerOrderEntity> savedOrderCaptor = ArgumentCaptor.forClass(CustomerOrderEntity.class);
        verify(orderRepository, times(1)).save(savedOrderCaptor.capture());
        verify(outboxService, times(1)).saveOrderCreatedEvent(savedOrderCaptor.getValue());
    }

    @Test
    void createOrderThrowsWhenCatalogValidationFails() {
        //given
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(),
                restaurantId,
                List.of(new CreateOrderItemRequest(menuItemId, 1))
        );

        when(catalogValidationClient.isOrderValid(restaurantId, List.of(menuItemId))).thenReturn(false);

        //when
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(CatalogValidationException.class);

        //then
        verify(orderRepository, never()).save(any());
        verify(outboxService, never()).saveOrderCreatedEvent(any());
    }

    @Test
    void getOrderThrowsWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getCustomerOrdersSuccessfully() {
        //given
        UUID customerId = UUID.randomUUID();

        CustomerOrderEntity order = new CustomerOrderEntity(customerId, UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 1);

        when(orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId, Pageable.unpaged()))
                .thenReturn(List.of(order));

        //when
        List<OrderResponse> result = orderService.getCustomerOrders(customerId, Pageable.unpaged());

        //then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(response -> {
                    assertThat(response.id()).isEqualTo(order.getId());
                    assertThat(response.customerId()).isEqualTo(customerId);
                });

        verify(orderRepository, times(1)).findAllByCustomerIdOrderByCreatedAtDesc(customerId, Pageable.unpaged());
    }
}
