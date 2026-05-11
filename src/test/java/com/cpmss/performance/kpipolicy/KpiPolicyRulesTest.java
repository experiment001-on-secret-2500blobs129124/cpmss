package com.cpmss.performance.kpipolicy;

import com.cpmss.organization.department.Department;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.KpiScoreRange;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies KPI policy tier rules that protect score-band version contracts.
 */
class KpiPolicyRulesTest {

    private final KpiPolicyRules rules = new KpiPolicyRules();

    @Test
    void rejectsOverlappingTierInSameDepartmentPolicyVersion() {
        KpiScoreRange proposed = KpiScoreRange.of(
                new BigDecimal("50.00"), new BigDecimal("80.00"));
        KpiPolicy existing = policy(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, 1, 1),
                "40.00", "60.00");

        assertThatThrownBy(() -> rules.validateNoTierOverlap(
                proposed, List.of(existing), null))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                PerformanceErrorCode.KPI_TIER_OVERLAP));
    }

    @Test
    void rejectsPolicyScoreOutsideSelectedTierRange() {
        UUID departmentId = UUID.randomUUID();
        LocalDate effectiveDate = LocalDate.of(2026, 1, 1);
        KpiPolicy selected = policy(
                UUID.randomUUID(), departmentId, effectiveDate, "80.00", "100.00");

        assertThatThrownBy(() -> rules.validatePolicyMatchesRecord(
                selected,
                departmentId,
                effectiveDate,
                selected.getScoreRange(),
                KpiScore.of(new BigDecimal("70.00"))))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                PerformanceErrorCode.KPI_POLICY_SCORE_OUT_OF_RANGE));
    }

    private static KpiPolicy policy(UUID policyId,
                                    UUID departmentId,
                                    LocalDate effectiveDate,
                                    String min,
                                    String max) {
        Department department = new Department();
        department.setId(departmentId);
        KpiPolicy policy = KpiPolicy.builder()
                .department(department)
                .effectiveDate(effectiveDate)
                .minKpiScore(KpiScore.of(new BigDecimal(min)))
                .maxKpiScore(KpiScore.of(new BigDecimal(max)))
                .build();
        policy.setId(policyId);
        return policy;
    }
}
