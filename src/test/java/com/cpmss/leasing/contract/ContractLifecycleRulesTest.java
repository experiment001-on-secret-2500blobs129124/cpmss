package com.cpmss.leasing.contract;

import com.cpmss.leasing.common.ContractStatus;
import com.cpmss.leasing.common.LeasingErrorCode;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContractLifecycleRulesTest {

    private final ContractRules rules = new ContractRules();

    @Test
    void activeContractsCanTerminateButTerminatedContractsCannotReactivate() {
        assertThat(ContractStatus.ACTIVE.canTransitionTo(ContractStatus.TERMINATED)).isTrue();

        assertThatThrownBy(() -> rules.validateStatusTransition(
                ContractStatus.TERMINATED, ContractStatus.ACTIVE))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void draftContractsCannotBeMarkedRenewedDirectly() {
        assertThatThrownBy(() -> rules.validateStatusTransition(
                ContractStatus.DRAFT, ContractStatus.RENEWED))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                LeasingErrorCode.CONTRACT_STATUS_TRANSITION_INVALID));
    }
}
