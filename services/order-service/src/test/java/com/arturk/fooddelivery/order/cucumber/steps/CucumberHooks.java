package com.arturk.fooddelivery.order.cucumber.steps;

import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import com.arturk.fooddelivery.order.service.grpc.client.CatalogValidationClient;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;

import static org.mockito.Mockito.reset;

@RequiredArgsConstructor
public class CucumberHooks {

    private final CustomerOrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final CatalogValidationClient catalogValidationClient;

    @Before
    public void cleanDatabase() {
        outboxEventRepository.deleteAll();
        orderRepository.deleteAll();
        reset(catalogValidationClient);
    }
}
