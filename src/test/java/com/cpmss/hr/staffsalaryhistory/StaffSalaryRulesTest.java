package com.cpmss.hr.staffsalaryhistory;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies staff salary rules for positive amounts and position-band caps.
 */
class StaffSalaryRulesTest {

    private final StaffSalaryRules rules = new StaffSalaryRules();

    @Test
    void rejectsStaffMaximumSalaryAboveActivePositionBand() {
        assertThatThrownBy(() -> rules.validateWithinPositionMaximum(
                new BigDecimal("6000.00"), new BigDecimal("5000.00")))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                HrErrorCode.STAFF_SALARY_EXCEEDS_POSITION_MAX));
    }
}
