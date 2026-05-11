package com.cpmss.finance.common;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for finance records.
 */
public class FinanceAccessRules {

    /**
     * Requires broad finance authority.
     *
     * @param user current authenticated user
     */
    public void requireFinanceAuthority(CurrentUser user) {
        if (hasFinanceAuthority(user)) {
            return;
        }
        throw new ApiException(FinanceErrorCode.FINANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows finance authority or the linked person owner of a bank account.
     *
     * @param user    current authenticated user
     * @param account bank account being read
     */
    public void requireCanViewBankAccount(CurrentUser user, BankAccount account) {
        if (hasFinanceAuthority(user) || isOwnedPersonAccount(user, account)) {
            return;
        }
        throw new ApiException(FinanceErrorCode.FINANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Resolves investment-stake scope for finance users and investor self-service.
     *
     * <p>Finance authority may pass {@code null} to list all rows or provide an
     * investor filter. Investor users are always narrowed to their linked person
     * and cannot request another investor's stakes.
     *
     * @param user current authenticated user
     * @param requestedInvestorId optional requested investor person UUID
     * @return {@code null} for broad finance listing, otherwise the scoped investor UUID
     */
    public UUID resolveInvestmentStakeInvestorScope(CurrentUser user, UUID requestedInvestorId) {
        if (hasFinanceAuthority(user)) {
            return requestedInvestorId;
        }
        if (user.hasRole(SystemRole.INVESTOR)
                && user.personId() != null
                && (requestedInvestorId == null || user.personId().equals(requestedInvestorId))) {
            return user.personId();
        }
        throw new ApiException(FinanceErrorCode.FINANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows finance authority or the linked person to request their bank accounts.
     *
     * @param user           current authenticated user
     * @param accountOwnerId person UUID whose accounts are requested
     */
    public void requireCanListBankAccounts(CurrentUser user, UUID accountOwnerId) {
        if (accountOwnerId == null) {
            requireFinanceAuthority(user);
            return;
        }
        if (hasFinanceAuthority(user)
                || (user.personId() != null && user.personId().equals(accountOwnerId))) {
            return;
        }
        throw new ApiException(FinanceErrorCode.FINANCE_RECORD_ACCESS_DENIED);
    }

    private boolean isOwnedPersonAccount(CurrentUser user, BankAccount account) {
        return account.getAccountOwner() != null
                && user.personId() != null
                && user.personId().equals(account.getAccountOwner().getId());
    }

    private boolean hasFinanceAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.ACCOUNTANT);
    }
}
