package com.cpmss.finance.bankaccount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an existing bank account.
 *
 * <p>All fields are replaceable. Exactly one owner must be set.
 *
 * @param bankName       the bank institution name
 * @param iban           the International Bank Account Number (optional)
 * @param swiftCode      the SWIFT/BIC code (optional)
 * @param isPrimary      whether this is the primary account for the owner
 * @param compoundId     the compound owner UUID (may be {@code null})
 * @param accountOwnerId the person owner UUID (may be {@code null})
 * @param companyId      the company owner UUID (may be {@code null})
 */
public record UpdateBankAccountRequest(
        @NotBlank @Size(max = 100) String bankName,
        @Size(max = 34) String iban,
        @Size(max = 11) String swiftCode,
        Boolean isPrimary,
        UUID compoundId,
        UUID accountOwnerId,
        UUID companyId
) {}
