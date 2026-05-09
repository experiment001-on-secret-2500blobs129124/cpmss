package com.cpmss.workforce.taskmonthlysalary.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.value.YearMonthPeriod;

import java.util.UUID;

/**
 * Response payload for a monthly payroll rollup record.
 *
 * @param staffId          the staff member's person UUID
 * @param departmentId     the department UUID
 * @param shiftId          the shift type UUID
 * @param payrollPeriod    the payroll period
 * @param monthlyDeduction monthly deduction money
 * @param monthlyBonus     monthly bonus money
 * @param tax              tax money deducted
 * @param monthlySalary    gross monthly salary money
 * @param monthlyNetSalary net monthly salary money after deductions
 */
public record TaskMonthlySalaryResponse(
        UUID staffId,
        UUID departmentId,
        UUID shiftId,
        YearMonthPeriod payrollPeriod,
        Money monthlyDeduction,
        Money monthlyBonus,
        Money tax,
        Money monthlySalary,
        Money monthlyNetSalary
) {}
