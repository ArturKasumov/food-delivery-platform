# Technical Improvements Roadmap

This document describes planned technical improvements, architectural enhancements,
known limitations, and technical debt of the system.


### REL-001: Improve the Outbox Publishing Retry Mechanism

Improve the retry mechanism used for publishing outbox events.

The improvement should include:

Adding a nextAttemptAt field to the outbox event.
Selecting events only when nextAttemptAt is less than or equal to the current time.
Supporting configurable retry delays.
Applying an exponential backoff strategy.
Preventing continuous retries without a delay.

### REL-002: Introduce a Terminal Status for Failed Outbox Events

Introduce a terminal DEAD status for outbox events that have reached the maximum number of publishing attempts.

The implementation should include:

Transitioning an event to DEAD after the retry limit is reached.
Events with the DEAD status must not be selected for further automatic retries.
Saving the latest publishing error.
Recording the time of the final failed attempt.
Providing a mechanism for manually reprocessing dead events.

### REL-003: Add Dead Letter Topics for Kafka Consumers

Introduce Dead Letter Topics (DLT) for Kafka consumers to handle messages 
that cannot be processed successfully.

The implementation should include:

A dedicated dead letter topic for each relevant Kafka topic.
Original message headers and payload preservation.
Error details and failure reason.
Retry attempt information.
Monitoring and alerting for messages sent to dead letter topics.

### REL-004: Add Retry Handling for PSP Checkout Session Creation

Add a retry mechanism for PSP calls performed by the Payment Service when creating a checkout session in CheckoutJobWorker.

### REL-005: Add Retry Handling for PSP Callback Delivery

Add a retry mechanism for callback requests sent by the PSP simulator.

### OBS-001: Preserve Trace Context Across the Transactional Outbox Boundary

Fix the trace context propagation gap between the original business operation and asynchronous outbox event publishing.

Currently, the trace created during the incoming request ends 
after the outbox event is persisted. When the scheduler later reads and publishes the event,
the operation starts as a separate, unrelated trace.

### OBS-002: Add Kafka Metrics

Add metrics for Kafka producers, consumers, and message processing.

### DB-001: Introduce an Outbox Event Retention Policy

Add a scheduled cleanup mechanism with configurable retention policy
for successfully published outbox events.

### DB-002: Introduce a Processed Event Retention Policy

Add a scheduled cleanup mechanism with configurable retention policy
for processed event records used for consumer idempotency.
