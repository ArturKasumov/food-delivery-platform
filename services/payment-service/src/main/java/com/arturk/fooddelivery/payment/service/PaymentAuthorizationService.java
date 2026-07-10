package com.arturk.fooddelivery.payment.service;

import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.dto.PaymentAuthorizationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentAuthorizationService {

    private static final BigDecimal MAX_AUTOMATIC_APPROVAL_AMOUNT = new BigDecimal("1000.00");

    public PaymentAuthorizationResult authorize(PaymentEntity payment) {
        if (payment.getAmount().compareTo(MAX_AUTOMATIC_APPROVAL_AMOUNT) <= 0) {
            return PaymentAuthorizationResult.succeed();
        }

        return PaymentAuthorizationResult.failed("Amount exceeds mock authorization limit");
    }
}
