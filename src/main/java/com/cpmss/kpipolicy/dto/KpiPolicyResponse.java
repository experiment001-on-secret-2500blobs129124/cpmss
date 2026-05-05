package com.cpmss.kpipolicy.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a KPI policy tier.
 *
 * @param id            the policy tier's UUID
 * @param departmentId  the department UUID
 * @param effectiveDate when this policy takes effect
 * @param tierLabel     label
 * @param minKpiScore   minimum KPI score
 * @param maxKpiScore   maximum KPI score
 * @param bonusRate     bonus rate multiplier
 * @param deductionRate deduction rate multiplier
 * @param approvedById  the approving manager UUID
 * @param createdAt     when the policy was created
 * @param updatedAt     when the policy was last modified
 */
public record KpiPolicyResponse(
        UUID id,
        UUID departmentId,
        LocalDate effectiveDate,
        String tierLabel,
        BigDecimal minKpiScore,
        BigDecimal maxKpiScore,
        BigDecimal bonusRate,
        BigDecimal deductionRate,
        UUID approvedById,
        Instant createdAt,
        Instant updatedAt
) {}
