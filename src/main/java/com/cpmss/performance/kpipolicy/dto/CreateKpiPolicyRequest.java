package com.cpmss.performance.kpipolicy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a KPI policy tier.
 *
 * @param departmentId the department UUID
 * @param effectiveDate when this policy takes effect
 * @param tierLabel     label (e.g. "Excellent", "Good", "Poor")
 * @param minKpiScore   minimum KPI score for this tier (inclusive)
 * @param maxKpiScore   maximum KPI score for this tier (inclusive)
 * @param bonusRate     bonus rate multiplier
 * @param deductionRate deduction rate multiplier
 * @param approvedById  the manager who approved the policy
 */
public record CreateKpiPolicyRequest(
        @NotNull UUID departmentId,
        @NotNull LocalDate effectiveDate,
        @NotBlank @Size(max = 50) String tierLabel,
        @NotNull BigDecimal minKpiScore,
        @NotNull BigDecimal maxKpiScore,
        BigDecimal bonusRate,
        BigDecimal deductionRate,
        @NotNull UUID approvedById
) {}
