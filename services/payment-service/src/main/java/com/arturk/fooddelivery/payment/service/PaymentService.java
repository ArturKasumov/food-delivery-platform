package com.arturk.fooddelivery.payment.service;

import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.payment.domain.CheckoutJobEntity;
import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.domain.ProcessedEventEntity;
import com.arturk.fooddelivery.payment.dto.response.PaymentResponse;
import com.arturk.fooddelivery.payment.exception.business.PaymentNotFoundException;
import com.arturk.fooddelivery.payment.repository.CheckoutJobRepository;
import com.arturk.fooddelivery.payment.repository.PaymentRepository;
import com.arturk.fooddelivery.payment.repository.ProcessedEventRepository;
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
    private final CheckoutJobRepository checkoutJobRepository;

    @Transactional
    public void processOrderCreatedEvent(OrderCreatedEvent event) {
        UUID eventId = UUID.fromString(event.getMetadata().getEventId());

        if (processedEventRepository.existsById(eventId)) {
            log.warn("Skipping already processed event {}", eventId);
            return;
        }

        UUID orderId = UUID.fromString(event.getPayload().getOrderId());

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            processedEventRepository.save(new ProcessedEventEntity(eventId, event.getMetadata().getEventType()));
            log.info("Skipping payment creation because payment already exists for order {}", orderId);
            return;
        }

        UUID customerId = UUID.fromString(event.getPayload().getCustomerId());
        BigDecimal amount = new BigDecimal(event.getPayload().getTotalAmount());
        String correlationId = event.getMetadata().getCorrelationId();

        PaymentEntity payment = paymentRepository.save(
                new PaymentEntity(orderId, customerId, amount)
        );

        checkoutJobRepository.save(new CheckoutJobEntity(payment.getId(), payment.getOrderId(), payment.getAmount(), correlationId));

        processedEventRepository.save(
                new ProcessedEventEntity(eventId, event.getMetadata().getEventType())
        );

        log.info("Initialized payment: {} and checkout job for order: {}",
                payment.getId(), payment.getOrderId());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(PaymentNotFoundException::new);
    }
}
