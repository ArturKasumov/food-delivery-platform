package com.arturk.fooddelivery.payment.config;

import com.arturk.fooddelivery.payment.constants.CorrelationIdConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(CorrelationIdConstants.HEADER_NAME);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
        response.setHeader(CorrelationIdConstants.HEADER_NAME, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CorrelationIdConstants.MDC_KEY);
        }
    }
}
