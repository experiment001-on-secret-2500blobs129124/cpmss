package com.cpmss.workforce.taskmonthlysalary;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.people.person.Person;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import jakarta.persistence.Column;
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

import java.math.BigDecimal;

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
    @JoinColumn(name = "staff_id")
    private Person staff;

    /** The department (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /** The payroll year (part of composite PK). */
    @Id
    @Column(name = "year")
    private Integer year;

    /** The payroll month (part of composite PK). */
    @Id
    @Column(name = "month")
    private Integer month;

    /** The shift type for this payroll period. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftAttendanceType shift;

    /** Monthly deduction amount (frozen snapshot). */
    @Column(name = "monthly_deduction", precision = 12, scale = 2)
    private BigDecimal monthlyDeduction;

    /** Monthly bonus amount (frozen snapshot). */
    @Column(name = "monthly_bonus", precision = 12, scale = 2)
    private BigDecimal monthlyBonus;

    /** Tax amount deducted (frozen snapshot). */
    @Column(name = "tax", precision = 12, scale = 2)
    private BigDecimal tax;

    /** Gross monthly salary (frozen snapshot). */
    @Column(name = "monthly_salary", precision = 12, scale = 2)
    private BigDecimal monthlySalary;

    /** Net monthly salary after deductions (frozen snapshot). */
    @Column(name = "monthly_net_salary", precision = 12, scale = 2)
    private BigDecimal monthlyNetSalary;
}
