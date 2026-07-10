package com.arturk.fooddelivery.catalog.service.grpc;

import com.arturk.fooddelivery.catalog.service.validation.CatalogValidationService;
import com.arturk.fooddelivery.catalog.dto.grpc.OrderItemValidationRequest;
import com.arturk.fooddelivery.catalog.dto.grpc.OrderValidationResult;
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
    public void validateOrder(ValidateOrderRequest request,
                              StreamObserver<ValidateOrderResponse> responseObserver)
    {

        UUID restaurantId = UUID.fromString(request.getRestaurantId());
        List<OrderItemValidationRequest> orderItems = request.getOrderItemsList()
                .stream()
                .map(item ->
                        new OrderItemValidationRequest(UUID.fromString(item.getMenuItemId()), item.getQuantity()))
                .toList();

        OrderValidationResult result = catalogValidationService.validateOrder(restaurantId, orderItems);

        responseObserver.onNext(ValidateOrderResponse.newBuilder()
                .setValid(result.valid())
                .setTotalAmount(result.totalAmount().toPlainString())
                .build());
        responseObserver.onCompleted();
    }
}
