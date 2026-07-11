package com.arturk.fooddelivery.psp.controller;

import com.arturk.fooddelivery.psp.enums.CheckoutDecision;
import com.arturk.fooddelivery.psp.model.CheckoutSession;
import com.arturk.fooddelivery.psp.service.CheckoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CheckoutViewController {

    private final CheckoutSessionService checkoutSessionService;

    @GetMapping("/checkout/{sessionId}")
    public String showCheckout(@PathVariable UUID sessionId, Model model) {
        CheckoutSession session = checkoutSessionService.getSession(sessionId);
        model.addAttribute("checkoutSession", session);
        return "checkout";
    }

    @PostMapping("/checkout/{sessionId}/pay")
    public String pay(@PathVariable UUID sessionId, Model model) {
        CheckoutSession session = checkoutSessionService.confirm(sessionId);
        model.addAttribute("checkoutSession", session);
        model.addAttribute("decision", CheckoutDecision.PAID);
        model.addAttribute("callbackUrl", session.callbackUrl());
        return "checkout-result";
    }

    @PostMapping("/checkout/{sessionId}/cancel")
    public String cancel(@PathVariable UUID sessionId, Model model) {
        CheckoutSession session = checkoutSessionService.cancel(sessionId);
        model.addAttribute("checkoutSession", session);
        model.addAttribute("decision", CheckoutDecision.CANCELLED);
        model.addAttribute("callbackUrl", session.callbackUrl());
        return "checkout-result";
    }
}
