# Technical Improvements Roadmap

This document describes planned technical improvements, architectural enhancements,
known limitations, and technical debt of the system.

### REL-003: Add Dead Letter Topics for Kafka Consumers

Introduce Dead Letter Topics (DLT) for Kafka consumers to handle messages 
that cannot be processed successfully.

The implementation should include:

A dedicated dead letter topic for each relevant Kafka topic.
Original message headers and payload preservation.
Error details and failure reason.
Retry attempt information.
Monitoring and alerting for messages sent to dead letter topics.

### REL-005: Improve Persistence and Callback Reliability in the PSP Simulator Service

Replace the in-memory checkout session storage based on ConcurrentHashMap with persistent database storage.
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
