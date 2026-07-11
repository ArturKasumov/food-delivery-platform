package com.arturk.fooddelivery.psp.controller;

import com.arturk.fooddelivery.psp.dto.CreateCheckoutSessionRequest;
import com.arturk.fooddelivery.psp.dto.CreateCheckoutSessionResponse;
import com.arturk.fooddelivery.psp.model.CheckoutSession;
import com.arturk.fooddelivery.psp.service.CheckoutSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutSessionController {

    private final CheckoutSessionService checkoutSessionService;

    @PostMapping("/session")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCheckoutSessionResponse createCheckoutSession(@Valid @RequestBody CreateCheckoutSessionRequest request) {
        CheckoutSession session = checkoutSessionService.createSession(request);
        return new CreateCheckoutSessionResponse(session.id(), session.checkoutUrl());
    }
}
