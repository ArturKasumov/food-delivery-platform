Feature: Order feature

  Scenario: Order created successfully
    Given catalog data is valid for a customer order
    When the customer creates the order
      | Field               | Value       |
      | customerId          | 10000000-0000-0000-0000-111100000001 |
      | restaurantId        | 10000000-0000-0000-0000-111100000002 |
      | items[0].menuItemId | 10000000-0000-0000-0000-111100000003 |
      | items[0].quantity   | 1           |
    Then the operation is successful
    Then the order should be saved with next fields
      | Field               | Value                                |
      | customerId          | 10000000-0000-0000-0000-111100000001 |
      | restaurantId        | 10000000-0000-0000-0000-111100000002 |
      | status              | PENDING_PAYMENT                      |
      | items[0].menuItemId | 10000000-0000-0000-0000-111100000003 |
      | items[0].quantity   | 1                                    |
    And an order created outbox event should be saved
