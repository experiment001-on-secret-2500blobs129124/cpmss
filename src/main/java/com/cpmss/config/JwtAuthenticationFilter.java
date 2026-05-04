package com.cpmss.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Intercepts every HTTP request to validate the JWT Bearer token.
 *
 * <p>If a valid access token is present in the {@code Authorization}
 * header, this filter sets the {@code SecurityContext} with the user's
 * email and role authority. Requests without a token pass through
 * unauthenticated — Spring Security's access rules decide what happens.
 *
 * @see JwtUtils
 * @see SecurityConfig
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;

    /**
     * Constructs the filter with the JWT utility dependency.
     *
     * @param jwtUtils JWT token generation and validation utility
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * Extracts and validates the Bearer token, then sets the security context.
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

        String authHeader = request.getHeader(HEADER);
        if (authHeader != null && authHeader.startsWith(PREFIX)) {
            String token = authHeader.substring(PREFIX.length());
            Claims claims = jwtUtils.validateToken(token);

            if (claims != null && jwtUtils.isAccessToken(claims)) {
                String email = jwtUtils.getEmail(claims);
                String role = jwtUtils.getRole(claims);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
