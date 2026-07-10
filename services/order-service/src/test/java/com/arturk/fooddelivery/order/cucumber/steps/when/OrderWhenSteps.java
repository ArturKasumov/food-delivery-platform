package com.arturk.fooddelivery.order.cucumber.steps.when;

import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.cucumber.common.FillObjectDataTable;
import com.arturk.fooddelivery.order.cucumber.context.CreateOrderScenarioContext;
import com.arturk.fooddelivery.order.dto.request.CreateOrderRequest;
import com.arturk.fooddelivery.order.dto.response.OrderResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RequiredArgsConstructor
public class OrderWhenSteps {

    private final CreateOrderScenarioContext context;
    private final TestRestTemplate restTemplate;

    @When("the customer creates the order")
    public void customerCreatesTheOrder(DataTable dataTable) {
        CreateOrderRequest request = new FillObjectDataTable().createObject(dataTable, CreateOrderRequest.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(CorrelationIdConstants.HEADER_NAME, UUID.randomUUID().toString());

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
                "/api/v1/orders",
                new HttpEntity<>(request, headers),
                OrderResponse.class
        );

        context.setResponse(response);
    }
}
