package com.arturk.fooddelivery.order.cucumber.steps.then;

import com.arturk.fooddelivery.order.cucumber.common.FillObjectDataTable;
import com.arturk.fooddelivery.order.cucumber.context.CreateOrderScenarioContext;
import com.arturk.fooddelivery.order.cucumber.data.CustomerOrderData;
import com.arturk.fooddelivery.order.cucumber.data.OrderItemData;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.domain.OrderItemEntity;
import com.arturk.fooddelivery.order.dto.response.OrderResponse;
import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import com.arturk.fooddelivery.order.exception.business.OrderNotFoundException;
import com.arturk.fooddelivery.order.repository.CustomerOrderRepository;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CustomerOrderThenSteps {

    private final CreateOrderScenarioContext context;
    private final CustomerOrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;

    @Then("the operation is successful")
    public void operationIsSuccessful() {
        assertThat(context.getResponse().getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Then("the order should be saved with next fields")
    public void orderShouldBeSavedWithNextFields(DataTable dataTable) {
        CustomerOrderData expectedOrderData = new CustomerOrderData();
        new FillObjectDataTable().fillObject(dataTable, expectedOrderData);

        OrderResponse response = context.getResponse().getBody();
        if (response == null) {
            throw new RuntimeException("Response is missing");
        }
        UUID orderId = response.id();
        context.setOrderId(orderId);

        CustomerOrderEntity order = orderRepository.findOrderWithItemsById(orderId).orElseThrow(OrderNotFoundException::new);
        assertExpectedOrder(order, expectedOrderData);
    }

    @Then("an order created outbox event should be saved")
    public void orderCreatedOutboxEventShouldBeSaved() {
        assertThat(outboxEventRepository.findAll())
                .filteredOn(event -> event.getAggregateId().equals(context.getOrderId()))
                .filteredOn(event -> event.getEventType().equals(ORDER_CREATED_EVENT_TYPE))
                .singleElement()
                .satisfies(event -> {
                    assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
                    assertThat(event.getCorrelationId()).isNotBlank();
                });
    }

    private void assertExpectedOrder(CustomerOrderEntity customerOrder, CustomerOrderData expectedOrder) {
        if (expectedOrder.getCustomerId() != null) {
            assertThat(customerOrder.getCustomerId()).isEqualTo(expectedOrder.getCustomerId());
        }
        if (expectedOrder.getRestaurantId() != null) {
            assertThat(customerOrder.getRestaurantId()).isEqualTo(expectedOrder.getRestaurantId());
        }
        if (expectedOrder.getStatus() != null) {
            assertThat(customerOrder.getStatus()).isEqualTo(expectedOrder.getStatus());
        }
        if (CollectionUtils.isNotEmpty(expectedOrder.getItems())) {
            assertExpectedOrderItems(customerOrder.getItems(), expectedOrder.getItems());
        }
    }

    private void assertExpectedOrderItems(List<OrderItemEntity> orderItems, List<OrderItemData> expectedItems) {
        expectedItems.forEach(expectedItem -> assertThat(orderItems)
                .anySatisfy(orderItem -> {
                    if (expectedItem.getMenuItemId() != null) {
                        assertThat(orderItem.getMenuItemId()).isEqualTo(expectedItem.getMenuItemId());
                    }
                    if (expectedItem.getQuantity() != null) {
                        assertThat(orderItem.getQuantity()).isEqualTo(expectedItem.getQuantity());
                    }
                }));
    }
}
