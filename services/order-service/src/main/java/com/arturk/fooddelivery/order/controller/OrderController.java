package com.arturk.fooddelivery.order.controller;

import com.arturk.fooddelivery.order.dto.request.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.response.OrderCreatedResponse;
import com.arturk.fooddelivery.order.dto.response.OrderResponse;
import com.arturk.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderResponse> getCustomerOrders(@RequestParam UUID customerId, Pageable pageable) {
        return orderService.getCustomerOrders(customerId, pageable);
    }
}
