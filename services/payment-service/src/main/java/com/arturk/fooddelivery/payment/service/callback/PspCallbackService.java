package com.arturk.fooddelivery.payment.service.callback;

import com.arturk.fooddelivery.payment.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.payment.dto.psp.PspPaymentCallbackRequest;
import com.arturk.fooddelivery.payment.exception.technical.SerializationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthorizationException;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PspCallbackService {

    private final ObjectMapper objectMapper;
    private final PspCallbackSignatureVerifier signatureVerifier;
    private final PaymentCallbackHandler paymentCallbackHandler;

    public void handlePspCallback(String timestamp, String signature, byte[] rawBody) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable(
                CorrelationIdConstants.MDC_KEY,
                extractCorrelationIdFromCallback(rawBody))) {

            verifySignature(timestamp, signature, rawBody);
            PspPaymentCallbackRequest callback = parseCallback(rawBody);
            paymentCallbackHandler.applyPspCallback(callback);
        }
    }

    private void verifySignature(String timestamp, String signature, byte[] rawBody) {
        if (!signatureVerifier.isValid(timestamp, signature, rawBody)) {
            log.warn("Rejected PSP callback with invalid signature");
            throw new AuthorizationException("Invalid signature");
        }
    }

    private PspPaymentCallbackRequest parseCallback(byte[] rawBody) {
        try {
            return objectMapper.readValue(
                    rawBody,
                    PspPaymentCallbackRequest.class
            );
        } catch (IOException exception) {
            log.warn("Rejected malformed PSP webhook", exception);
            throw new SerializationException(exception.getMessage());
        }
    }

    private String extractCorrelationIdFromCallback(byte[] rawBody) {
        try {
            JsonNode callback = objectMapper.readTree(rawBody);

            if (callback == null) {
                throw new SerializationException("PSP callback body is empty");
            }

            JsonNode correlationIdNode = callback
                    .path("metadata")
                    .path(CorrelationIdConstants.MDC_KEY);

            if (!correlationIdNode.isTextual() || correlationIdNode.isEmpty()) {
                return UUID.randomUUID().toString();
            }

            return correlationIdNode.textValue();
        } catch (IOException exception) {
            throw new SerializationException(exception.getMessage());
        }
    }
}
