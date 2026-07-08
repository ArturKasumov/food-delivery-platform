package com.arturk.fooddelivery.order.cucumber.context;

import com.arturk.fooddelivery.order.dto.OrderResponse;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ScenarioScope
@Getter
@Setter
public class CreateOrderScenarioContext {

    private UUID orderId;
    private ResponseEntity<OrderResponse> response;
}
