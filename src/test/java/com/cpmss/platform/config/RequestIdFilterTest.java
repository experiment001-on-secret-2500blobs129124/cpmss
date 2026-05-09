package com.cpmss.platform.config;

import com.cpmss.platform.common.ErrorResponseFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the {@link RequestIdFilter} contract for request ID
 * propagation, MDC lifecycle, and safe header acceptance.
 *
 * <p>Covers: client-sent UUID reuse, server-generated UUID when
 * header is missing or unsafe, response header propagation, and
 * MDC cleanup after the filter chain completes.
 */
@DisplayName("RequestIdFilter")
class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    @DisplayName("Generates UUID when no X-Request-Id header is present")
    void generatesIdWhenMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        String responseId = response.getHeader(RequestIdFilter.HEADER);
        assertNotNull(responseId, "Response must include X-Request-Id");
        assertTrue(responseId.matches(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"),
                "Generated ID must be a UUID");
    }

    @Test
    @DisplayName("Reuses client-sent UUID when safe")
    void reusesClientUuid() throws Exception {
        String clientId = "550e8400-e29b-41d4-a716-446655440000";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestIdFilter.HEADER, clientId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertEquals(clientId, response.getHeader(RequestIdFilter.HEADER));
    }

    @Test
    @DisplayName("Rejects non-UUID header and generates new ID")
    void rejectsUnsafeHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestIdFilter.HEADER, "not-a-uuid; DROP TABLE");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        String responseId = response.getHeader(RequestIdFilter.HEADER);
        assertNotNull(responseId);
        assertTrue(responseId.matches(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"),
                "Unsafe header should be replaced with a generated UUID");
    }

    @Test
    @DisplayName("Rejects header that exceeds 36 characters")
    void rejectsTooLongHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestIdFilter.HEADER,
                "550e8400-e29b-41d4-a716-446655440000-extra-stuff");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        String responseId = response.getHeader(RequestIdFilter.HEADER);
        assertNotNull(responseId);
        assertEquals(36, responseId.length(), "Should generate a standard UUID");
    }

    @Test
    @DisplayName("MDC is cleared after filter chain completes")
    void mdcClearedAfterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertNull(MDC.get(ErrorResponseFactory.MDC_REQUEST_ID),
                "MDC requestId must be cleared after the filter chain");
    }

    @Test
    @DisplayName("MDC contains requestId during filter chain execution")
    void mdcSetDuringChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String[] capturedId = new String[1];

        MockFilterChain chain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req,
                                 jakarta.servlet.ServletResponse res) {
                capturedId[0] = MDC.get(ErrorResponseFactory.MDC_REQUEST_ID);
            }
        };

        filter.doFilterInternal(request, response, chain);

        assertNotNull(capturedId[0], "MDC requestId must be set during chain");
        assertEquals(response.getHeader(RequestIdFilter.HEADER), capturedId[0]);
    }
}
