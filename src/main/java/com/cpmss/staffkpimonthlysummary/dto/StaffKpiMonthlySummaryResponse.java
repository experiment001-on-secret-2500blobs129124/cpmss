package com.cpmss.staffkpimonthlysummary.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response payload for a monthly KPI summary record.
 *
 * @param staffId           the staff member's person UUID
 * @param departmentId      the department UUID
 * @param year              the summary year
 * @param month             the summary month
 * @param avgKpiScore       average KPI score for the month
 * @param totalKpiScore     total KPI score for the month
 * @param daysScored        number of days scored
 * @param applicableTier    the tier label applied at close time
 * @param payrollBonusRate  bonus rate from policy
 * @param payrollDeductRate deduction rate from policy
 * @param kpiPolicyId       the KPI policy used at close time
 * @param closedById        the manager who closed this summary
 */
public record StaffKpiMonthlySummaryResponse(
        UUID staffId,
        UUID departmentId,
        Integer year,
        Integer month,
        BigDecimal avgKpiScore,
        BigDecimal totalKpiScore,
        Integer daysScored,
        String applicableTier,
        BigDecimal payrollBonusRate,
        BigDecimal payrollDeductRate,
        UUID kpiPolicyId,
        UUID closedById
) {}
