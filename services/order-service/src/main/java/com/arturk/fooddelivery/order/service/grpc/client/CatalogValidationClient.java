package com.arturk.fooddelivery.order.service.grpc.client;

import com.arturk.fooddelivery.contracts.catalog.v1.CatalogValidationServiceGrpc;
import com.arturk.fooddelivery.contracts.catalog.v1.ValidateOrderRequest;
import com.arturk.fooddelivery.contracts.catalog.v1.ValidateOrderResponse;
import com.arturk.fooddelivery.order.exception.technical.CatalogServiceUnavailableException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogValidationClient {

    private final CatalogValidationServiceGrpc.CatalogValidationServiceBlockingStub catalogValidationStub;

    public boolean isOrderValid(UUID restaurantId, List<UUID> menuItemIds) {
        ValidateOrderRequest request = ValidateOrderRequest.newBuilder()
                .setRestaurantId(restaurantId.toString())
                .addAllMenuItemIds(menuItemIds.stream().map(UUID::toString).toList())
                .build();

        try {
            ValidateOrderResponse response = catalogValidationStub.validateOrder(request);
            return response.getValid();
        } catch (StatusRuntimeException exception) {
            throw new CatalogServiceUnavailableException(exception.getStatus().toString());
        }
    }
}
