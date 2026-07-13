package com.arturk.fooddelivery.payment.service.callback;

import com.arturk.fooddelivery.payment.config.properties.PspProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
@Slf4j
public class PspCallbackSignatureVerifier {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final PspProperties properties;

    public boolean isValid(String timestamp, String signature, byte[] rawBody) {
        try {
            Instant signedAt = Instant.ofEpochSecond(Long.parseLong(timestamp));
            Duration age = Duration.between(signedAt, Instant.now()).abs();
            if (age.compareTo(properties.callbackTimestampTolerance()) > 0) {
                return false;
            }

            byte[] expected = calculate(timestamp, rawBody);
            byte[] actual = HexFormat.of().parseHex(signature);
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception exception) {
            log.warn("Failed to verify PSP callback signature", exception);
            return false;
        }
    }

    private byte[] calculate(String timestamp, byte[] rawBody) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(
                properties.callbackSecret().getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        ));
        mac.update(timestamp.getBytes(StandardCharsets.UTF_8));
        mac.update((byte) '.');
        return mac.doFinal(rawBody);
    }
}
