package com.cpmss.exception;

/**
 * Thrown when a domain business rule is violated.
 *
 * <p>Maps to HTTP 422 Unprocessable Entity. Use this when an operation
 * passes format validation but violates a business invariant — for example,
 * "a person must have at least one role".
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new {@code BusinessException} with the given message.
     *
     * @param message description of the violated rule
     */
    public BusinessException(String message) {
        super(message);
    }
}
