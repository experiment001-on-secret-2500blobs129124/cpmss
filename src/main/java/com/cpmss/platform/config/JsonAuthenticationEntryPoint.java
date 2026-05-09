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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a JSON error response for unauthenticated requests.
 *
 * <p>Replaces Spring Security's default HTML 401 response with the
 * standard {@link ErrorResponse} envelope. Uses
 * {@link CommonErrorCode#AUTHENTICATION_REQUIRED} as the stable code.
 *
 * @see JsonAccessDeniedHandler
 * @see SecurityConfig
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(
            JsonAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;

    /**
     * Constructs the entry point with the shared Jackson mapper.
     *
     * @param objectMapper Jackson object mapper for JSON serialization
     */
    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Writes a 401 JSON error response.
     *
     * @param request       the rejected request
     * @param response      the HTTP response to write to
     * @param authException the authentication failure
     * @throws IOException if writing the response fails
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("auth.authentication.required uri={}", request.getRequestURI());

        ErrorResponse body = ErrorResponseFactory.fromErrorCode(
                CommonErrorCode.AUTHENTICATION_REQUIRED);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
