package com.arturk.fooddelivery.psp.service;

import com.arturk.fooddelivery.psp.dto.CreateCheckoutSessionRequest;
import com.arturk.fooddelivery.psp.exception.SessionNotFoundException;
import com.arturk.fooddelivery.psp.model.CheckoutSession;
import com.arturk.fooddelivery.psp.enums.CheckoutSessionStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

@Service
public class CheckoutSessionService {

    private final Map<UUID, CheckoutSession> sessions = new ConcurrentHashMap<>();

    public CheckoutSession createSession(CreateCheckoutSessionRequest request) {
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

        sessions.put(sessionId, session);
        return session;
    }

    public CheckoutSession getSession(UUID sessionId) {
        CheckoutSession session = sessions.get(sessionId);
        if (session == null) {
            throw new SessionNotFoundException();
        }

        return session;
    }

    public CheckoutSession confirm(UUID sessionId) {
        return updateSession(sessionId, CheckoutSession::markPaid);
    }

    public CheckoutSession cancel(UUID sessionId) {
        return updateSession(sessionId, CheckoutSession::markCancelled);
    }

    private CheckoutSession updateSession(UUID sessionId, UnaryOperator<CheckoutSession> updater) {
        return sessions.compute(sessionId, (id, current) -> {
            if (current == null) {
                throw new SessionNotFoundException();
            }
            return updater.apply(current);
        });
    }

}
