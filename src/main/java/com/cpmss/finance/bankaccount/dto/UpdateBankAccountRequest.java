package com.cpmss.finance.bankaccount.dto;

import com.cpmss.finance.bankaccount.Iban;
import com.cpmss.finance.bankaccount.SwiftCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an existing bank account.
 *
 * <p>All fields are replaceable. Exactly one owner must be set.
 *
 * @param bankName       the bank institution name
 * @param iban           the validated International Bank Account Number (optional)
 * @param swiftCode      the validated SWIFT/BIC code (optional)
 * @param isPrimary      whether this is the primary account for the owner
 * @param compoundId     the compound owner UUID (may be {@code null})
 * @param accountOwnerId the person owner UUID (may be {@code null})
 * @param companyId      the company owner UUID (may be {@code null})
 */
public record UpdateBankAccountRequest(
        @NotBlank @Size(max = 100) String bankName,
        @Valid Iban iban,
        @Valid SwiftCode swiftCode,
        Boolean isPrimary,
        UUID compoundId,
        UUID accountOwnerId,
        UUID companyId
) {}
