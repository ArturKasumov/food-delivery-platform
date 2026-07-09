package com.arturk.fooddelivery.catalog.service.grpc;

import com.arturk.fooddelivery.catalog.service.validation.CatalogValidationService;
import com.arturk.fooddelivery.contracts.catalog.v1.CatalogValidationServiceGrpc;
import com.arturk.fooddelivery.contracts.catalog.v1.ValidateOrderRequest;
import com.arturk.fooddelivery.contracts.catalog.v1.ValidateOrderResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CatalogValidationGrpcService extends CatalogValidationServiceGrpc.CatalogValidationServiceImplBase {

    private final CatalogValidationService catalogValidationService;

    @Override
    public void isOrderValid(
            ValidateOrderRequest request,
            StreamObserver<ValidateOrderResponse> responseObserver
    ) {

        UUID restaurantId = UUID.fromString(request.getRestaurantId());
        List<UUID> menuItemIds = request.getMenuItemIdsList()
                .stream()
                .map(UUID::fromString)
                .toList();

        boolean isValid = catalogValidationService.isOrderValid(restaurantId, menuItemIds);

        responseObserver.onNext(ValidateOrderResponse.newBuilder().setValid(isValid).build());
        responseObserver.onCompleted();
    }
}
