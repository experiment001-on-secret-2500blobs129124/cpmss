package com.cpmss.platform.common;

import java.time.Instant;

/**
 * Standard response envelope for all REST endpoints.
 *
 * <p>Controllers return {@code ApiResponse<T>} — never raw entities or DTOs.
 *
 * @param <T> payload type
 */
public record ApiResponse<T>(
        int status,
        String message,
        T data,
        Instant timestamp
) {

    /**
     * Creates a 200 OK response with the given payload.
     *
     * @param data the response payload
     * @param <T>  payload type
     * @return a 200 OK {@code ApiResponse}
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "Success", data, Instant.now());
    }

    /**
     * Creates a 201 Created response with the given payload.
     *
     * @param data the newly created resource
     * @param <T>  payload type
     * @return a 201 Created {@code ApiResponse}
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Created", data, Instant.now());
    }

    /**
     * Creates a 204 No Content response with a null payload.
     *
     * @param <T> payload type
     * @return a 204 No Content {@code ApiResponse}
     */
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, "No Content", null, Instant.now());
    }
}
