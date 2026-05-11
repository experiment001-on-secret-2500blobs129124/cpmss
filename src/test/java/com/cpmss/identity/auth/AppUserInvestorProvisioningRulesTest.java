package com.cpmss.identity.auth;

import com.cpmss.identity.common.IdentityErrorCode;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies investor-account provisioning authority rules.
 */
class AppUserInvestorProvisioningRulesTest {

    private final AppUserRules rules = new AppUserRules();

    @Test
    void accountantCanProvisionInvestorAccounts() {
        assertThatCode(() -> rules.validateProvisioningScope(
                SystemRole.ACCOUNTANT, SystemRole.INVESTOR))
                .doesNotThrowAnyException();
    }

    @Test
    void accountantCannotProvisionInternalStaffAccounts() {
        assertThatThrownBy(() -> rules.validateProvisioningScope(
                SystemRole.ACCOUNTANT, SystemRole.STAFF))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(IdentityErrorCode.AUTHORITY_INSUFFICIENT));
    }

    @Test
    void hrOfficerCannotProvisionInvestorAccounts() {
        assertThatThrownBy(() -> rules.validateProvisioningScope(
                SystemRole.HR_OFFICER, SystemRole.INVESTOR))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(IdentityErrorCode.AUTHORITY_INSUFFICIENT));
    }
}
