package com.cpmss.performance.staffkpimonthlysummary;

import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Protects KPI month-end close authorization inputs.
 */
class StaffKpiMonthlySummaryRulesTest {

    private final StaffKpiMonthlySummaryRules rules = new StaffKpiMonthlySummaryRules();

    /**
     * Month-end close must record who authorized the close.
     */
    @Test
    void closeRequiresCloser() {
        assertThatThrownBy(() -> rules.validateCloserProvided(false))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                PerformanceErrorCode.KPI_CLOSER_REQUIRED));
    }

    /**
     * A provided closer allows the service to continue into aggregation.
     */
    @Test
    void acceptsProvidedCloser() {
        assertThatCode(() -> rules.validateCloserProvided(true)).doesNotThrowAnyException();
    }
}
