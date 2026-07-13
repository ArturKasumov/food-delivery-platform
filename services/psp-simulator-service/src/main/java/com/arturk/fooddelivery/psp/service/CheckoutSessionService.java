package com.arturk.fooddelivery.psp.service;

import com.arturk.fooddelivery.psp.dto.request.CreateCheckoutSessionRequest;
import com.arturk.fooddelivery.psp.enums.CheckoutDecision;
import com.arturk.fooddelivery.psp.exception.SessionNotFoundException;
import com.arturk.fooddelivery.psp.model.CheckoutSession;
import com.arturk.fooddelivery.psp.enums.CheckoutSessionStatus;
import com.arturk.fooddelivery.psp.service.client.PspCallbackClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class CheckoutSessionService {

    private final PspCallbackClient pspCallbackClient;
    private final Map<UUID, CheckoutSession> checkoutSessions = new ConcurrentHashMap<>();
    private final Map<UUID, CheckoutSession> checkoutSessionsByPaymentId = new ConcurrentHashMap<>();

    public CheckoutSession createSession(CreateCheckoutSessionRequest request) {
        return checkoutSessionsByPaymentId.computeIfAbsent(
                request.paymentId(),
                ignored -> createNewSession(request)
        );
    }

    public CheckoutSession getSession(UUID sessionId) {
        CheckoutSession session = checkoutSessions.get(sessionId);
        if (session == null) {
            throw new SessionNotFoundException();
        }

        return session;
    }

    public CheckoutSession confirm(UUID sessionId) {
        CheckoutSession checkoutSession = updateSession(sessionId, CheckoutSession::markPaid);
        pspCallbackClient.send(checkoutSession, CheckoutDecision.PAID);
        return checkoutSession;
    }

    public CheckoutSession cancel(UUID sessionId) {
        CheckoutSession checkoutSession = updateSession(sessionId, CheckoutSession::markCancelled);
        pspCallbackClient.send(checkoutSession, CheckoutDecision.CANCELLED);
        return checkoutSession;
    }

    private CheckoutSession updateSession(UUID sessionId, UnaryOperator<CheckoutSession> updater) {
        return checkoutSessions.compute(sessionId, (id, current) -> {
            if (current == null) {
                throw new SessionNotFoundException();
            }

            CheckoutSession updated = updater.apply(current);
            checkoutSessionsByPaymentId.put(updated.paymentId(), updated);
            return updated;
        });
    }

    private CheckoutSession createNewSession(CreateCheckoutSessionRequest request) {
        UUID sessionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CheckoutSession session = new CheckoutSession(
                sessionId,
                request.paymentId(),
                request.orderId(),
                request.amount(),
                request.currency(),
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/checkout/{sessionId}")
                        .build(sessionId)
                        .toString(),
                request.callbackUrl(),
                CheckoutSessionStatus.CREATED,
                now,
                now
        );

        checkoutSessions.put(sessionId, session);
        return session;
    }

}
