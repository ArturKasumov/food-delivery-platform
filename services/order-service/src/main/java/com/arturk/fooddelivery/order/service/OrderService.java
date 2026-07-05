package com.arturk.fooddelivery.order.service;

import com.arturk.fooddelivery.order.dto.CreateOrderItemRequest;
import com.arturk.fooddelivery.order.service.grpc.client.CatalogValidationClient;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.dto.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.OrderResponse;
import com.arturk.fooddelivery.order.exception.business.CatalogValidationException;
import com.arturk.fooddelivery.order.exception.business.OrderNotFoundException;
import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
import com.arturk.fooddelivery.order.service.outbox.OutboxService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final CatalogValidationClient catalogValidationClient;
    private final OutboxService orderOutboxService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        validateOrder(request);

        CustomerOrderEntity order = new CustomerOrderEntity(request.customerId(), request.restaurantId());
        request.items().forEach(item -> order.addItem(item.menuItemId(), item.quantity()));

        CustomerOrderEntity savedOrder = orderRepository.save(order);
        orderOutboxService.saveOrderCreatedEvent(savedOrder);
        log.info("Order created: {}", savedOrder.getId());

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        return OrderResponse.from(getOrderById(orderId));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(UUID customerId, Pageable pageable) {
        log.info("Finding customer orders for customer {}", customerId);
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    private CustomerOrderEntity getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    private void validateOrder(CreateOrderRequest request) {
        boolean isOrderValid = catalogValidationClient.isOrderValid(
                request.restaurantId(),
                request.items().stream()
                        .map(CreateOrderItemRequest::menuItemId)
                        .toList()
        );

        if (!isOrderValid) {
            throw new CatalogValidationException();
        }
    }
}
