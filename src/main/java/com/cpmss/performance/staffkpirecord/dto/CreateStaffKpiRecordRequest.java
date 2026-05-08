package com.cpmss.performance.staffkpirecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for recording a daily KPI score.
 *
 * @param staffId      the staff member's person UUID
 * @param departmentId the department UUID
 * @param recordDate   the date of the KPI assessment
 * @param kpiScore     the KPI score (non-negative)
 * @param kpiPolicyId  the applicable KPI policy tier UUID
 * @param recordedById the manager who recorded this score
 * @param notes        optional evaluator notes
 */
public record CreateStaffKpiRecordRequest(
        @NotNull UUID staffId,
        @NotNull UUID departmentId,
        @NotNull LocalDate recordDate,
        @NotNull @PositiveOrZero BigDecimal kpiScore,
        @NotNull UUID kpiPolicyId,
        @NotNull UUID recordedById,
        String notes
) {}
