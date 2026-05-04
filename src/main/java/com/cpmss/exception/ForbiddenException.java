package com.cpmss.exception;

/**
 * Thrown when the authenticated user lacks permission to perform an action.
 *
 * <p>Maps to HTTP 403 Forbidden. Distinct from 401 Unauthorized — the user
 * is known, but not allowed to access this resource.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Constructs a new {@code ForbiddenException} with the given message.
     *
     * @param message description of why access was denied
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
