package com.cpmss.performance.kpipolicy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request payload for updating an existing KPI policy tier.
 *
 * @param tierLabel     label
 * @param minKpiScore   minimum KPI score
 * @param maxKpiScore   maximum KPI score
 * @param bonusRate     bonus rate multiplier
 * @param deductionRate deduction rate multiplier
 */
public record UpdateKpiPolicyRequest(
        @NotBlank @Size(max = 50) String tierLabel,
        @NotNull BigDecimal minKpiScore,
        @NotNull BigDecimal maxKpiScore,
        BigDecimal bonusRate,
        BigDecimal deductionRate
) {}
