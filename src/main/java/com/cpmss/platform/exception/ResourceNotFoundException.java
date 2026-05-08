package com.cpmss.platform.exception;

import java.util.UUID;

/**
 * Thrown when a requested resource does not exist in the database.
 *
 * <p>Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a {@code ResourceNotFoundException} using a UUID identifier.
     *
     * @param resourceName the entity type name (e.g. {@code "Person"})
     * @param id           the UUID that was looked up
     */
    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " with ID " + id + " not found");
    }

    /**
     * Constructs a {@code ResourceNotFoundException} using a string identifier
     * such as a slug or email.
     *
     * @param resourceName the entity type name (e.g. {@code "Person"})
     * @param identifier   the string identifier that was looked up
     */
    public ResourceNotFoundException(String resourceName, String identifier) {
        super(resourceName + " '" + identifier + "' not found");
    }
}
