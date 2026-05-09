package com.cpmss.finance.common;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

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
        if (hasFinanceAuthority(user)
                || (account.getAccountOwner() != null
                && user.personId() != null
                && user.personId().equals(account.getAccountOwner().getId()))) {
            return;
        }
        throw new ApiException(FinanceErrorCode.FINANCE_RECORD_ACCESS_DENIED);
    }

    private boolean hasFinanceAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.ACCOUNTANT);
    }
}
