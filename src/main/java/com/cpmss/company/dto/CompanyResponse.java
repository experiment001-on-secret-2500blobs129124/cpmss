package com.cpmss.company.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a company.
 *
 * <p>Exposes all user-visible company fields plus audit timestamps.
 *
 * @param id          the company's UUID primary key
 * @param companyName the company's legal or trading name
 * @param taxId       tax identification number (may be {@code null})
 * @param phoneNo     contact phone number (may be {@code null})
 * @param companyType classification (may be {@code null})
 * @param createdAt   when the company was created
 * @param updatedAt   when the company was last modified
 */
public record CompanyResponse(
        UUID id,
        String companyName,
        String taxId,
        String phoneNo,
        String companyType,
        Instant createdAt,
        Instant updatedAt
) {}
