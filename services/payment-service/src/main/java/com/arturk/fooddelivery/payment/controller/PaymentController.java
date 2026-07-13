package com.arturk.fooddelivery.payment.controller;

import com.arturk.fooddelivery.payment.dto.response.PaymentResponse;
import com.arturk.fooddelivery.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public PaymentResponse getPaymentByOrderId(@RequestParam UUID orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }
}
