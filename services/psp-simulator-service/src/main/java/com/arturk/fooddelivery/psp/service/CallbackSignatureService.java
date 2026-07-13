package com.arturk.fooddelivery.psp.service;

import com.arturk.fooddelivery.psp.config.properties.PspCallbackProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class CallbackSignatureService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final PspCallbackProperties properties;

    public String sign(String timestamp, byte[] rawBody) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(
                    properties.callbackSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            ));
            mac.update(timestamp.getBytes(StandardCharsets.UTF_8));
            mac.update((byte) '.');
            return HexFormat.of().formatHex(mac.doFinal(rawBody));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to sign PSP callback", exception);
        }
    }
}
