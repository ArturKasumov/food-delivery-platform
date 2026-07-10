package com.arturk.fooddelivery.payment.service;

import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.domain.ProcessedEventEntity;
import com.arturk.fooddelivery.payment.dto.PaymentAuthorizationResult;
import com.arturk.fooddelivery.payment.repository.PaymentRepository;
import com.arturk.fooddelivery.payment.repository.ProcessedEventRepository;
import com.arturk.fooddelivery.payment.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final PaymentAuthorizationService paymentAuthorizationService;
    private final OutboxService outboxService;

    @Transactional
    public void processOrderCreatedEvent(OrderCreatedEvent event) {
        UUID eventId = UUID.fromString(event.getMetadata().getEventId());

        if (processedEventRepository.existsById(eventId)) {
            log.warn("Skipping already processed event {}", eventId);
            return;
        }

        UUID orderId = UUID.fromString(event.getPayload().getOrderId());
        UUID customerId = UUID.fromString(event.getPayload().getCustomerId());
        BigDecimal amount = new BigDecimal(event.getPayload().getTotalAmount());

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            processedEventRepository.save(new ProcessedEventEntity(eventId, event.getMetadata().getEventType()));
            log.info("Skipping payment creation because payment already exists for order {}", orderId);
            return;
        }

        PaymentEntity payment = new PaymentEntity(orderId, customerId, amount);
        PaymentAuthorizationResult authorizationResult = paymentAuthorizationService.authorize(payment);

        if (authorizationResult.isSuccessful()) {
            payment.complete();
        } else {
            payment.fail(authorizationResult.failureReason());
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);
        outboxService.savePaymentResultEvent(savedPayment);
        processedEventRepository.save(new ProcessedEventEntity(eventId, event.getMetadata().getEventType()));

        log.info("Processed payment {} for order {} with status {}",
                savedPayment.getId(),
                savedPayment.getOrderId(),
                savedPayment.getStatus());
    }
}
