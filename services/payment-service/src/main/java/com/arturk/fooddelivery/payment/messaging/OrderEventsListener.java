package com.arturk.fooddelivery.payment.messaging;

import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.payment.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(
        topics = "${app.kafka.topics.order-events}"
)
public class OrderEventsListener {

    private final PaymentService paymentService;

    @KafkaHandler
    public void onOrderCreated(OrderCreatedEvent event) {
        withCorrelationId(
                event.getMetadata().getCorrelationId(),
                () -> paymentService.processOrderCreatedEvent(event)
        );
    }

    @KafkaHandler(isDefault = true)
    public void onUnsupported(Object event) {
        log.debug(
                "Ignoring unsupported order event: {}",
                event.getClass().getSimpleName()
        );
    }

    private void withCorrelationId(String correlationId, Runnable action) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable(
                CorrelationIdConstants.MDC_KEY,
                correlationId
        )) {
            action.run();
        }
    }
}
