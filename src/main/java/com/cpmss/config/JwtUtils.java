package com.cpmss.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Generates and validates JWT access and refresh tokens.
 *
 * <p>Access tokens carry the user's email as {@code subject} and
 * {@code role} as a custom claim. Refresh tokens carry only the
 * email and a {@code type=refresh} claim.
 *
 * @see JwtProperties
 * @see JwtAuthenticationFilter
 */
@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey signingKey;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    /**
     * Constructs {@code JwtUtils} from externalized JWT properties.
     *
     * @param properties JWT configuration bound from {@code application.yml}
     */
    public JwtUtils(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = properties.accessTokenExpiryMs();
        this.refreshTokenExpiryMs = properties.refreshTokenExpiryMs();
    }

    /**
     * Generates a short-lived access token.
     *
     * @param email the user's email (becomes the JWT subject)
     * @param role  the user's system role (stored as a custom claim)
     * @return a signed JWT access token string
     */
    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Generates a long-lived refresh token.
     *
     * @param email the user's email (becomes the JWT subject)
     * @return a signed JWT refresh token string
     */
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiryMs))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates a token and returns its claims.
     *
     * @param token the JWT string to validate
     * @return the parsed claims, or {@code null} if the token is invalid
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the email (subject) from a validated token's claims.
     *
     * @param claims the parsed JWT claims
     * @return the email stored as the subject
     */
    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    /**
     * Extracts the system role from a validated token's claims.
     *
     * @param claims the parsed JWT claims
     * @return the role string (e.g. {@code "ADMIN"})
     */
    public String getRole(Claims claims) {
        return claims.get(CLAIM_ROLE, String.class);
    }

    /**
     * Checks whether the given claims represent an access token.
     *
     * @param claims the parsed JWT claims
     * @return true if the token type is {@code "access"}
     */
    public boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    /**
     * Checks whether the given claims represent a refresh token.
     *
     * @param claims the parsed JWT claims
     * @return true if the token type is {@code "refresh"}
     */
    public boolean isRefreshToken(Claims claims) {
        return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }
}
