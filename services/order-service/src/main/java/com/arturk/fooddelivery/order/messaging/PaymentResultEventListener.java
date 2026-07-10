package com.arturk.fooddelivery.order.messaging;

import com.arturk.fooddelivery.contracts.avro.payment.v1.PaymentCompletedEvent;
import com.arturk.fooddelivery.contracts.avro.payment.v1.PaymentFailedEvent;
import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.service.OrderService;
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
        topics = "${app.kafka.topics.payment-events}"
)
public class PaymentResultEventListener {

    private final OrderService orderService;

    @KafkaHandler
    public void onPaymentCompletedEvent(PaymentCompletedEvent event) {
        withCorrelationId(
                event.getMetadata().getCorrelationId(),
                () -> orderService.processPaymentCompletedEvent(event)
        );
    }

    @KafkaHandler
    public void onPaymentFailedEvent(PaymentFailedEvent event) {
        withCorrelationId(
                event.getMetadata().getCorrelationId(),
                () -> orderService.processPaymentFailedEvent(event)
        );
    }

    @KafkaHandler(isDefault = true)
    public void onUnsupportedEvent(Object event) {
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
