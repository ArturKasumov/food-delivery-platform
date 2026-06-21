package com.arturk.fooddelivery.catalog.config;

import com.arturk.fooddelivery.catalog.constants.CorrelationIdConstants;
import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.slf4j.MDC;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@GlobalServerInterceptor
@Component
public class GrpcCorrelationIdServerInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> CORRELATION_ID_METADATA_KEY =
            Metadata.Key.of(
                    CorrelationIdConstants.HEADER_NAME,
                    Metadata.ASCII_STRING_MARSHALLER
            );

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String incomingCorrelationId = headers.get(CORRELATION_ID_METADATA_KEY);

        String correlationId = incomingCorrelationId == null || incomingCorrelationId.isBlank()
                ? UUID.randomUUID().toString()
                : incomingCorrelationId;

        ServerCall<ReqT, RespT> correlationIdServerCall =
                new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
                    @Override
                    public void close(Status status, Metadata trailers) {
                        MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
                        try {
                            super.close(status, trailers);
                        } finally {
                            MDC.remove(CorrelationIdConstants.MDC_KEY);
                        }
                    }
                };

        ServerCall.Listener<ReqT> listener;
        boolean started = false;
        MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
        try {
            listener = next.startCall(correlationIdServerCall, headers);
            started = true;
        } finally {
            if (started) {
                MDC.remove(CorrelationIdConstants.MDC_KEY);
            }
        }

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onMessage(ReqT message) {
                runWithCorrelationId(correlationId, () -> super.onMessage(message));
            }

            @Override
            public void onHalfClose() {
                runWithCorrelationId(correlationId, super::onHalfClose);
            }

            @Override
            public void onCancel() {
                runWithCorrelationId(correlationId, super::onCancel);
            }

            @Override
            public void onComplete() {
                runWithCorrelationId(correlationId, super::onComplete);
            }

            @Override
            public void onReady() {
                runWithCorrelationId(correlationId, super::onReady);
            }
        };
    }

    private void runWithCorrelationId(String correlationId, Runnable action) {
        boolean completed = false;
        MDC.put(CorrelationIdConstants.MDC_KEY, correlationId);
        try {
            action.run();
            completed = true;
        } finally {
            if (completed) {
                MDC.remove(CorrelationIdConstants.MDC_KEY);
            }
        }
    }
}
