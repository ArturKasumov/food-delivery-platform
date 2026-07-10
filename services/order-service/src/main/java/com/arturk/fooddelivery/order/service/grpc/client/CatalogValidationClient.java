package com.arturk.fooddelivery.order.service.grpc.client;

import com.arturk.fooddelivery.contracts.catalog.v1.CatalogValidationServiceGrpc;
import com.arturk.fooddelivery.contracts.catalog.v1.OrderItem;
import com.arturk.fooddelivery.contracts.catalog.v1.ValidateOrderRequest;
import com.arturk.fooddelivery.contracts.catalog.v1.ValidateOrderResponse;
import com.arturk.fooddelivery.order.dto.CatalogOrderValidationResult;
import com.arturk.fooddelivery.order.dto.request.CreateOrderItemRequest;
import com.arturk.fooddelivery.order.exception.technical.CatalogServiceUnavailableException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogValidationClient {

    private final CatalogValidationServiceGrpc.CatalogValidationServiceBlockingStub catalogValidationStub;

    public CatalogOrderValidationResult validateOrder(UUID restaurantId, List<CreateOrderItemRequest> items) {
        ValidateOrderRequest request = ValidateOrderRequest.newBuilder()
                .setRestaurantId(restaurantId.toString())
                .addAllOrderItems(items.stream()
                        .map(item -> OrderItem.newBuilder()
                                .setMenuItemId(item.menuItemId().toString())
                                .setQuantity(item.quantity())
                                .build())
                        .toList())
                .build();

        try {
            ValidateOrderResponse response = catalogValidationStub.validateOrder(request);
            return new CatalogOrderValidationResult(response.getValid(), new BigDecimal(response.getTotalAmount()));
        } catch (StatusRuntimeException exception) {
            throw new CatalogServiceUnavailableException(exception.getStatus().toString());
        }
    }
}
