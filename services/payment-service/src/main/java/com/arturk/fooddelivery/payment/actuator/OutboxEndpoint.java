package com.arturk.fooddelivery.payment.actuator;

import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;
import com.arturk.fooddelivery.payment.enums.OutboxEventStatus;
import com.arturk.fooddelivery.payment.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
@Endpoint(id = "outbox")
@RequiredArgsConstructor
@Slf4j
public class OutboxEndpoint {

    private final OutboxService outboxService;

    @WriteOperation
    public WebEndpointResponse<ReprocessResponse> reprocess(@Selector UUID eventId) {
        try {
            OutboxEventEntity event = outboxService.reprocessDeadEvent(eventId);
            log.info("Manually reprocessing outbox event: {}, eventType: {}, aggregateId: {}",
                    event.getId(), event.getEventType(), event.getAggregateId());

            return new WebEndpointResponse<>(ReprocessResponse.from(event), WebEndpointResponse.STATUS_OK);
        } catch (NoSuchElementException exception) {
            return new WebEndpointResponse<>(null, WebEndpointResponse.STATUS_NOT_FOUND);
        } catch (IllegalStateException exception) {
            return new WebEndpointResponse<>(null, 409);
        }
    }

    public record ReprocessResponse(
            UUID eventId,
            OutboxEventStatus status,
            int retryAttempt,
            LocalDateTime nextAttemptAt
    ) {
        private static ReprocessResponse from(OutboxEventEntity event) {
            return new ReprocessResponse(
                    event.getId(),
                    event.getStatus(),
                    event.getRetryAttempt(),
                    event.getNextAttemptAt()
            );
        }
    }
}
