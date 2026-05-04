package com.cpmss.exception;

/**
 * Thrown when a create or update would violate a uniqueness constraint.
 *
 * <p>Maps to HTTP 409 Conflict — for example, duplicate email or duplicate slug.
 */
public class ConflictException extends RuntimeException {

    /**
     * Constructs a new {@code ConflictException} with the given message.
     *
     * @param message description of the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}
