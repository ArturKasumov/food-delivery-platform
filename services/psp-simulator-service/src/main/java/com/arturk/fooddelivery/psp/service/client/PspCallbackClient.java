package com.arturk.fooddelivery.psp.service.client;

import com.arturk.fooddelivery.psp.dto.request.PspPaymentCallbackRequest;
import com.arturk.fooddelivery.psp.enums.CheckoutDecision;
import com.arturk.fooddelivery.psp.model.CheckoutSession;
import com.arturk.fooddelivery.psp.service.CallbackSignatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PspCallbackClient {

    public static final String TIMESTAMP_HEADER = "X-PSP-Timestamp";
    public static final String SIGNATURE_HEADER = "X-PSP-Signature";

    private final RestClient pspCallbackRestClient;
    private final ObjectMapper objectMapper;
    private final CallbackSignatureService callbackSignatureService;

    public void send(CheckoutSession session, CheckoutDecision decision) {
        PspPaymentCallbackRequest callback = new PspPaymentCallbackRequest(
                UUID.randomUUID(),
                session.id(),
                session.paymentId(),
                session.orderId(),
                decision,
                Instant.now()
        );

        try {
            byte[] rawBody = objectMapper.writeValueAsBytes(callback);
            String timestamp = Long.toString(Instant.now().getEpochSecond());
            String signature = callbackSignatureService.sign(timestamp, rawBody);

            RestClient.RequestBodySpec request = pspCallbackRestClient.post()
                    .uri(session.callbackUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(TIMESTAMP_HEADER, timestamp)
                    .header(SIGNATURE_HEADER, signature);

            request.body(rawBody).retrieve().toBodilessEntity();
            log.info("Delivered PSP callback: {} for payment: {}", callback.eventId(), session.paymentId());
        } catch (Exception exception) {
            log.warn("Failed to deliver PSP callback for payment: {}", session.paymentId(), exception);
        }
    }
}
