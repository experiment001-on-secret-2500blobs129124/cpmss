package com.cpmss.workforce.taskmonthlysalary.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response payload for a monthly payroll rollup record.
 *
 * @param staffId          the staff member's person UUID
 * @param departmentId     the department UUID
 * @param shiftId          the shift type UUID
 * @param year             the payroll year
 * @param month            the payroll month
 * @param monthlyDeduction monthly deduction amount
 * @param monthlyBonus     monthly bonus amount
 * @param tax              tax deducted
 * @param monthlySalary    gross monthly salary
 * @param monthlyNetSalary net monthly salary after deductions
 */
public record TaskMonthlySalaryResponse(
        UUID staffId,
        UUID departmentId,
        UUID shiftId,
        Integer year,
        Integer month,
        BigDecimal monthlyDeduction,
        BigDecimal monthlyBonus,
        BigDecimal tax,
        BigDecimal monthlySalary,
        BigDecimal monthlyNetSalary
) {}
