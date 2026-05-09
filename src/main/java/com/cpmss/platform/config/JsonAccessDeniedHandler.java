package com.cpmss.platform.config;

import com.cpmss.platform.common.ErrorResponse;
import com.cpmss.platform.common.ErrorResponseFactory;
import com.cpmss.platform.exception.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a JSON error response when an authenticated user lacks permission.
 *
 * <p>Handles Spring Security route-level 403 rejections with the standard
 * {@link ErrorResponse} envelope. Uses {@link CommonErrorCode#ACCESS_DENIED}
 * as the stable code.
 *
 * <p>Service-level 403 errors thrown via {@code ApiException} with
 * {@code ACCESS_DENIED} are handled by {@code GlobalExceptionHandler},
 * not this handler.
 *
 * @see JsonAuthenticationEntryPoint
 * @see SecurityConfig
 */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(
            JsonAccessDeniedHandler.class);
    private final ObjectMapper objectMapper;

    /**
     * Constructs the handler with the shared Jackson mapper.
     *
     * @param objectMapper Jackson object mapper for JSON serialization
     */
    public JsonAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Writes a 403 JSON error response.
     *
     * @param request               the rejected request
     * @param response              the HTTP response to write to
     * @param accessDeniedException the authorization failure
     * @throws IOException if writing the response fails
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("authorization.route.denied uri={}", request.getRequestURI());

        ErrorResponse body = ErrorResponseFactory.fromErrorCode(
                CommonErrorCode.ACCESS_DENIED);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
