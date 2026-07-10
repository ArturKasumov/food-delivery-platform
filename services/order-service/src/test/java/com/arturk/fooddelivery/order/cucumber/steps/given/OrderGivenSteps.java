package com.arturk.fooddelivery.order.cucumber.steps.given;

import com.arturk.fooddelivery.order.dto.CatalogOrderValidationResult;
import com.arturk.fooddelivery.order.service.grpc.client.CatalogValidationClient;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class OrderGivenSteps {

    private final CatalogValidationClient catalogValidationClient;

    @Given("catalog data is valid for a customer order")
    public void catalogDataIsValidForCustomerOrder() {
        when(catalogValidationClient.validateOrder(any(UUID.class), anyList()))
                .thenReturn(new CatalogOrderValidationResult(true, new BigDecimal("100.00")));
    }
}
