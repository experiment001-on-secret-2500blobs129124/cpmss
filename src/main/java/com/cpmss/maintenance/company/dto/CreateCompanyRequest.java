package com.cpmss.maintenance.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a company.
 *
 * <p>Only {@code companyName} is required. Tax ID, phone, and type
 * are optional metadata captured when available.
 *
 * @param companyName the company's legal or trading name
 * @param taxId       optional tax identification number
 * @param phoneNo     optional contact phone number
 * @param companyType optional classification (e.g. Vendor, Contractor)
 */
public record CreateCompanyRequest(
        @NotBlank @Size(max = 150) String companyName,
        @Size(max = 50) String taxId,
        @Size(max = 20) String phoneNo,
        @Size(max = 50) String companyType
) {}
