package com.arturk.fooddelivery.order.config;

import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.slf4j.MDC;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.stereotype.Component;

@GlobalClientInterceptor
@Component
public class GrpcCorrelationIdClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> CORRELATION_ID_METADATA_KEY =
            Metadata.Key.of(CorrelationIdConstants.HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next
    ) {
        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
        return new ForwardingClientCall.SimpleForwardingClientCall<>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String correlationId = MDC.get(CorrelationIdConstants.MDC_KEY);
                if (correlationId != null && !correlationId.isBlank()) {
                    headers.put(CORRELATION_ID_METADATA_KEY, correlationId);
                }
                super.start(responseListener, headers);
            }
        };
    }
}
