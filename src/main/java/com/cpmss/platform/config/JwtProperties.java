package com.cpmss.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds JWT configuration from {@code application.yml}.
 *
 * <p>Properties under the {@code jwt} prefix are mapped here:
 * {@code jwt.secret}, {@code jwt.access-token-expiry-ms},
 * {@code jwt.refresh-token-expiry-ms}.
 *
 * @param secret             HMAC signing key (at least 256 bits)
 * @param accessTokenExpiryMs  access token lifetime in milliseconds
 * @param refreshTokenExpiryMs refresh token lifetime in milliseconds
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpiryMs,
        long refreshTokenExpiryMs
) {}
