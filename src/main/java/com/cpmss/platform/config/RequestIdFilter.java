package com.cpmss.platform.config;

import com.cpmss.platform.common.ErrorResponseFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Assigns a unique request ID to every HTTP request.
 *
 * <p>If the client sends a safe {@code X-Request-Id} header (UUID format,
 * at most 36 characters), the server reuses it. Otherwise the server
 * generates a new UUID. The request ID is:
 *
 * <ul>
 *   <li>placed into SLF4J MDC under key {@code requestId},</li>
 *   <li>included in the {@code X-Request-Id} response header,</li>
 *   <li>included in every {@link com.cpmss.platform.common.ErrorResponse}
 *       via {@link ErrorResponseFactory}.</li>
 * </ul>
 *
 * <p>MDC is always cleared in a {@code finally} block to prevent leaking
 * the request ID into unrelated threads.
 *
 * @see ErrorResponseFactory#MDC_REQUEST_ID
 */
@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {

    /** HTTP header name for request ID propagation. */
    public static final String HEADER = "X-Request-Id";

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final int MAX_HEADER_LENGTH = 36;

    /**
     * Assigns or reuses the request ID, sets MDC, and propagates
     * the ID to the response header.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the remaining filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String requestId = resolveRequestId(request);
            MDC.put(ErrorResponseFactory.MDC_REQUEST_ID, requestId);
            response.setHeader(HEADER, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(ErrorResponseFactory.MDC_REQUEST_ID);
        }
    }

    /**
     * Accepts the client-sent request ID if it is a safe UUID,
     * otherwise generates a new one.
     */
    private String resolveRequestId(HttpServletRequest request) {
        String clientId = request.getHeader(HEADER);
        if (clientId != null
                && clientId.length() <= MAX_HEADER_LENGTH
                && UUID_PATTERN.matcher(clientId).matches()) {
            return clientId;
        }
        return UUID.randomUUID().toString();
    }
}
