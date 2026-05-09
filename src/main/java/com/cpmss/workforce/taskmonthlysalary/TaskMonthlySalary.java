package com.cpmss.workforce.taskmonthlysalary;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.platform.common.value.YearMonthPeriod;
import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.organization.department.Department;
import com.cpmss.people.person.Person;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Detail entity for monthly payroll rollup per staff per department.
 *
 * <p>Composite PK: ({@code staff_id}, {@code department_id},
 * {@code year}, {@code month}). Monthly salary fields are payroll
 * snapshots — do not recalculate after month is closed.
 */
@Entity
@Table(name = "Task_Monthly_Salary")
@IdClass(TaskMonthlySalaryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskMonthlySalary extends BaseAuditEntity {

    /** The staff member (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Person staff;

    /** The department (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** The payroll year (part of composite PK). */
    @Id
    @Column(name = "year", nullable = false)
    private Integer year;

    /** The payroll month (part of composite PK). */
    @Id
    @Column(name = "month", nullable = false)
    private Integer month;

    /** The shift type for this payroll period. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftAttendanceType shift;

    /** Monthly deduction money (frozen snapshot). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "monthly_deduction", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "monthly_deduction_currency", length = 10))
    })
    private Money monthlyDeduction;

    /** Monthly bonus money (frozen snapshot). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "monthly_bonus", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "monthly_bonus_currency", length = 10))
    })
    private Money monthlyBonus;

    /** Tax money deducted (frozen snapshot). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "tax", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "tax_currency", length = 10))
    })
    private Money tax;

    /** Gross monthly salary money (frozen snapshot). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "monthly_salary", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "monthly_salary_currency", length = 10))
    })
    private Money monthlySalary;

    /** Net monthly salary money after deductions (frozen snapshot). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "monthly_net_salary", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "monthly_net_salary_currency", length = 10))
    })
    private Money monthlyNetSalary;

    /**
     * Returns the payroll period for domain logic.
     *
     * @return the payroll period
     */
    public YearMonthPeriod getPayrollPeriod() {
        return YearMonthPeriod.of(year, month);
    }

    /**
     * Assigns the payroll period from a validated value object.
     *
     * @param payrollPeriod the payroll period
     */
    public void setPayrollPeriod(YearMonthPeriod payrollPeriod) {
        if (payrollPeriod == null) {
            throw new ApiException(WorkforceErrorCode.PAYROLL_PERIOD_REQUIRED);
        }
        this.year = payrollPeriod.year();
        this.month = payrollPeriod.month();
    }
}
