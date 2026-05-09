package com.cpmss.platform.exception;

/**
 * Contract for all error codes in the system.
 *
 * <p>Each bounded context defines its own enum implementing this interface,
 * following the same pattern as {@link com.cpmss.platform.common.ApiPaths}
 * for REST paths. The error code carries the stable client-facing code,
 * the HTTP status, and a default human-readable message.
 *
 * <p>Error code names use upper snake case and follow the pattern
 * {@code DOMAIN_CONCEPT_FAILURE} — for example,
 * {@code MONEY_CURRENCY_MISMATCH} or {@code CONTRACT_TARGET_INVALID}.
 *
 * @see CommonErrorCode
 * @see ApiException
 */
public interface ErrorCode {

    /**
     * Stable upper-snake-case code sent to API clients.
     *
     * <p>This is the contract clients rely on. Human-readable messages
     * may change, but codes must remain stable across releases.
     *
     * @return the error code string, typically the enum constant name
     */
    String code();

    /**
     * HTTP status code this error maps to.
     *
     * @return the HTTP status (e.g. 400, 404, 409, 422)
     */
    int status();

    /**
     * Default human-readable message for this error.
     *
     * <p>Used when the throw site does not provide a context-specific
     * override. The message explains what went wrong in terms a client
     * or developer can understand.
     *
     * @return the default error message
     */
    String defaultMessage();
}
