package com.cpmss.finance.bankaccount;

import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Business rules for {@link BankAccount} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see BankAccountService
 */
public class BankAccountRules {

    /**
     * Validates that exactly one owner is set.
     *
     * <p>A bank account must be owned by exactly one of: a compound,
     * a person, or a company. Setting zero or more than one
     * owner violates the mutual exclusion constraint.
     *
     * @param compoundId     the compound owner UUID (may be {@code null})
     * @param accountOwnerId the person owner UUID (may be {@code null})
     * @param companyId      the company owner UUID (may be {@code null})
     * @throws ApiException if zero or more than one owner is set
     */
    public void validateExactlyOneOwner(UUID compoundId, UUID accountOwnerId, UUID companyId) {
        int count = 0;
        if (compoundId != null) count++;
        if (accountOwnerId != null) count++;
        if (companyId != null) count++;

        if (count != 1) {
            throw new ApiException(FinanceErrorCode.BANK_ACCOUNT_OWNER_INVALID);
        }
    }
}
