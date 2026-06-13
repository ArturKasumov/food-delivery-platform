package com.arturk.fooddelivery.catalog.config;

import com.arturk.fooddelivery.catalog.constants.CorrelationIdConstants;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void usesIncomingCorrelationId() throws ServletException, IOException {
        String correlationId = UUID.randomUUID().toString();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader(CorrelationIdConstants.HEADER_NAME, correlationId);

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(CorrelationIdConstants.HEADER_NAME)).isEqualTo(correlationId);
        assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
    }

    @Test
    void generatesCorrelationIdWhenHeaderIsMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(CorrelationIdConstants.HEADER_NAME))
                .isNotBlank()
                .satisfies(value -> assertThat(UUID.fromString(value)).isNotNull());
        assertThat(MDC.get(CorrelationIdConstants.MDC_KEY)).isNull();
    }
}
