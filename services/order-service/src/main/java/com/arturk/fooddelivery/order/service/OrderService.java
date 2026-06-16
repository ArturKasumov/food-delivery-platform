package com.arturk.fooddelivery.order.service;

import com.arturk.fooddelivery.order.domain.CustomerOrder;
import com.arturk.fooddelivery.order.dto.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.OrderResponse;
import com.arturk.fooddelivery.order.exception.OrderNotFoundException;
import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
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

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder(request.customerId(), request.restaurantId());
        request.items().forEach(item -> order.addItem(item.menuItemId(), item.quantity()));

        CustomerOrder savedOrder = orderRepository.save(order);
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

    private CustomerOrder getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }
}
