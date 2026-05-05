package com.cpmss.staffkpirecord.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a daily KPI record.
 *
 * @param staffId      the staff member's person UUID
 * @param departmentId the department UUID
 * @param recordDate   the date of the KPI assessment
 * @param kpiScore     the KPI score
 * @param kpiPolicyId  the KPI policy UUID
 * @param recordedById the manager who recorded this score
 * @param notes        evaluator notes
 */
public record StaffKpiRecordResponse(
        UUID staffId,
        UUID departmentId,
        LocalDate recordDate,
        BigDecimal kpiScore,
        UUID kpiPolicyId,
        UUID recordedById,
        String notes
) {}
