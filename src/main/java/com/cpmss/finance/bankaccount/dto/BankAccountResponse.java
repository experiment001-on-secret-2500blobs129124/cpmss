package com.cpmss.finance.bankaccount.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a bank account.
 *
 * <p>Includes owner IDs — exactly one will be non-null.
 *
 * @param id             the bank account's UUID primary key
 * @param bankName       the bank institution name
 * @param iban           the International Bank Account Number (may be {@code null})
 * @param swiftCode      the SWIFT/BIC code (may be {@code null})
 * @param isPrimary      whether this is the primary account for the owner
 * @param compoundId     the compound owner UUID (may be {@code null})
 * @param accountOwnerId the person owner UUID (may be {@code null})
 * @param companyId      the company owner UUID (may be {@code null})
 * @param createdAt      when the account was created
 * @param updatedAt      when the account was last modified
 */
public record BankAccountResponse(
        UUID id,
        String bankName,
        String iban,
        String swiftCode,
        Boolean isPrimary,
        UUID compoundId,
        UUID accountOwnerId,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt
) {}
