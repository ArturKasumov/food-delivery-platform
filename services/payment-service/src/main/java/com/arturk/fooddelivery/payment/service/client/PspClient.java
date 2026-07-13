package com.arturk.fooddelivery.payment.service.client;

import com.arturk.fooddelivery.payment.config.properties.PspProperties;
import com.arturk.fooddelivery.payment.domain.CheckoutJobEntity;
import com.arturk.fooddelivery.payment.dto.psp.CreateCheckoutSessionRequest;
import com.arturk.fooddelivery.payment.dto.psp.CreateCheckoutSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PspClient {

    private final RestClient pspRestClient;
    private final PspProperties pspProperties;

    public CreateCheckoutSessionResponse createCheckoutSession(CheckoutJobEntity checkoutJob) {
        CreateCheckoutSessionRequest request = new CreateCheckoutSessionRequest(
                checkoutJob.getPaymentId(),
                checkoutJob.getOrderId(),
                checkoutJob.getAmount(),
                "UAH",
                pspProperties.callbackUrl()
        );

        CreateCheckoutSessionResponse response = pspRestClient.post()
                .uri("/checkout/session")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CreateCheckoutSessionResponse.class);

        if (response == null) {
            throw new IllegalStateException("PSP checkout session response is empty");
        }

        return response;
    }
}
