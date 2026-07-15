package com.arturk.fooddelivery.payment.service.client;

import com.arturk.fooddelivery.payment.config.properties.PspProperties;
import com.arturk.fooddelivery.payment.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.payment.domain.CheckoutJobEntity;
import com.arturk.fooddelivery.payment.dto.psp.CreateCheckoutSessionRequest;
import com.arturk.fooddelivery.payment.dto.psp.CreateCheckoutSessionResponse;
import com.arturk.fooddelivery.payment.exception.technical.PspClientException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

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
                pspProperties.callbackUrl(),
                Map.of(CorrelationIdConstants.MDC_KEY, checkoutJob.getCorrelationId())
        );

        try {
            CreateCheckoutSessionResponse response = pspRestClient.post()
                    .uri("/checkout/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CreateCheckoutSessionResponse.class);

            if (response == null
                    || response.sessionId() == null
                    || StringUtils.isEmpty(response.checkoutUrl())) {
                throw new PspClientException("PSP checkout session response is invalid", false);
            }

            return response;
        } catch (ResourceAccessException exception) {
            throw new PspClientException(normalizeMessage(exception), true);
        } catch (RestClientResponseException exception) {
            int statusCode = exception.getStatusCode().value();
            boolean retryable = statusCode == 408 || statusCode == 429 || exception.getStatusCode().is5xxServerError();
            throw new PspClientException("PSP returned HTTP status: " + statusCode, retryable);
        } catch (RestClientException exception) {
            throw new PspClientException(normalizeMessage(exception), false);
        }
    }

    private String normalizeMessage(Exception exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
    }
}
