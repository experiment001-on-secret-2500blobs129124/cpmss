package com.cpmss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * CPMSS application entry point.
 *
 * <p>{@code @EnableJpaAuditing} activates Spring Data auditing so that
 * {@code createdAt}, {@code updatedAt}, {@code createdBy}, and
 * {@code updatedBy} are populated automatically on every save.
 * The auditor identity is supplied by
 * {@link com.cpmss.config.SecurityAuditorAware}.
 */
@SpringBootApplication
@EnableJpaAuditing
public class CpmssApplication {

    public static void main(String[] args) {
        SpringApplication.run(CpmssApplication.class, args);
    }
}
