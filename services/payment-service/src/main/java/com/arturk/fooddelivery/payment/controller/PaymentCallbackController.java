package com.arturk.fooddelivery.payment.controller;

import com.arturk.fooddelivery.payment.service.callback.PspCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/callback")
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackController {

    private static final String TIMESTAMP_HEADER = "X-PSP-Timestamp";
    private static final String SIGNATURE_HEADER = "X-PSP-Signature";

    private final PspCallbackService pspCallbackService;

    @PostMapping("/psp-simulator")
    public ResponseEntity<Void> handlePspCallback(@RequestHeader(TIMESTAMP_HEADER) String timestamp,
                                                  @RequestHeader(SIGNATURE_HEADER) String signature,
                                                  @RequestBody byte[] rawBody) {
        pspCallbackService.handlePspCallback(timestamp, signature, rawBody);
        return ResponseEntity.ok().build();
    }
}
