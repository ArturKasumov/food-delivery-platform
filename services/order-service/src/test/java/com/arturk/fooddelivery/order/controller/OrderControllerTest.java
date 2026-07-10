package com.arturk.fooddelivery.order.controller;

import com.arturk.fooddelivery.order.dto.request.CreateOrderItemRequest;
import com.arturk.fooddelivery.order.dto.request.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.response.OrderCreatedResponse;
import com.arturk.fooddelivery.order.dto.response.OrderItemResponse;
import com.arturk.fooddelivery.order.dto.response.OrderResponse;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.exception.business.CatalogValidationException;
import com.arturk.fooddelivery.order.exception.business.OrderNotFoundException;
import com.arturk.fooddelivery.order.exception.technical.CatalogServiceUnavailableException;
import com.arturk.fooddelivery.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(com.arturk.fooddelivery.order.exception.GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrderSuccessfully() throws Exception {
        //given
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                restaurantId,
                List.of(new CreateOrderItemRequest(menuItemId, 1))
        );

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(new OrderCreatedResponse(
                orderId
        ));

        //when //then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(120.00));
    }

    @Test
    void createOrderValidationError() throws Exception {
        //given
        CreateOrderRequest request = new CreateOrderRequest(
                null,
                null,
                List.of(new CreateOrderItemRequest(null, 1))
        );

        //when //then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER-MS-01-ERROR"))
                .andExpect(jsonPath("$.description").value("Request validation failed"))
                .andExpect(jsonPath("$.details", containsString("customerId")))
                .andExpect(jsonPath("$.details", containsString("restaurantId")))
                .andExpect(jsonPath("$.details", containsString("items[0].menuItemId")));
    }

    @Test
    void createOrderCatalogValidationFails() throws Exception {
        //given
        CreateOrderRequest request = getValidRequest();

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenThrow(new CatalogValidationException());

        //when //then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER-MS-03-ERROR"))
                .andExpect(jsonPath("$.description").value("Catalog validation failed."));
    }

    @Test
    void createOrderWhenCatalogServiceIsUnavailable() throws Exception {
        //given
        CreateOrderRequest request = getValidRequest();

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new CatalogServiceUnavailableException(Status.UNAVAILABLE.getCode().toString()));

        //when //then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("ORDER-MS-04-ERROR"))
                .andExpect(jsonPath("$.description").value("Catalog service is unavailable."))
                .andExpect(jsonPath("$.details").value(Status.UNAVAILABLE.getCode().toString()));
    }

    @Test
    void getOrderSuccessfully() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        when(orderService.getOrder(orderId)).thenReturn(new OrderResponse(
                orderId,
                customerId,
                restaurantId,
                OrderStatus.PENDING_PAYMENT,
                new BigDecimal("99.50"),
                List.of(new OrderItemResponse(UUID.randomUUID(), menuItemId, 1)),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        //when //then
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.status").value(OrderStatus.PENDING_PAYMENT.name()))
                .andExpect(jsonPath("$.totalAmount").value(99.50))
                .andExpect(jsonPath("$.items[0].menuItemId").value(menuItemId.toString()))
                .andExpect(jsonPath("$.items[0].quantity").value(1));
    }

    @Test
    void getOrderWhenOrderDoesNotExist() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();

        when(orderService.getOrder(orderId)).thenThrow(new OrderNotFoundException());

        //when //then
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER-MS-02-ERROR"))
                .andExpect(jsonPath("$.description").value("Order not found."));
    }

    @Test
    void getCustomerOrdersSuccessfully() throws Exception {
        //given
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(orderService.getCustomerOrders(eq(customerId), any(Pageable.class))).thenReturn(List.of(new OrderResponse(
                orderId,
                customerId,
                UUID.randomUUID(),
                OrderStatus.PENDING_PAYMENT,
                new BigDecimal("45.00"),
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        )));

        //when //then
        mockMvc.perform(get("/api/v1/orders")
                        .param("customerId", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderId.toString()))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()));
    }

    private CreateOrderRequest getValidRequest() {
        return new CreateOrderRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new CreateOrderItemRequest(UUID.randomUUID(), 1))
        );
    }
}
