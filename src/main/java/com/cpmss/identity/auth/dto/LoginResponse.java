package com.cpmss.identity.auth.dto;

/**
 * Response payload returned after a successful login or token refresh.
 *
 * @param accessToken  short-lived JWT for API access
 * @param refreshToken long-lived token for obtaining new access tokens
 * @param tokenType    always {@code "Bearer"}
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {

    /**
     * Creates a Bearer login response.
     *
     * @param accessToken  the access JWT
     * @param refreshToken the refresh JWT
     * @return a new {@code LoginResponse} with type "Bearer"
     */
    public static LoginResponse bearer(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken, "Bearer");
    }
}
