package com.cpmss.finance.payrollpayment;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.platform.common.value.YearMonthPeriod;
import com.cpmss.organization.department.Department;
import com.cpmss.finance.payment.Payment;
import com.cpmss.people.person.Person;
import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Detail entity (1:1 extension of {@link Payment}) for staff payroll payments.
 *
 * <p>PK = {@code payment_id} (shared with Payment).
 * Composite FK: ({@code staff_id}, {@code department_id}, {@code year},
 * {@code month}) → {@code Task_Monthly_Salary}.
 */
@Entity
@Table(name = "Payroll_Payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPayment extends BaseAuditEntity {

    /** Shared primary key — same as the payment's UUID. */
    @Id
    @Column(name = "payment_id", nullable = false)
    private UUID id;

    /** The parent payment record (1:1 relationship). */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    /** The staff member receiving the payroll. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Person staff;

    /** The department for this payroll period. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** The payroll year. */
    @Column(name = "year", nullable = false)
    private Integer year;

    /** The payroll month. */
    @Column(name = "month", nullable = false)
    private Integer month;

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
     * @throws ApiException if the period is missing
     */
    public void setPayrollPeriod(YearMonthPeriod payrollPeriod) {
        if (payrollPeriod == null) {
            throw new ApiException(FinanceErrorCode.PAYROLL_PERIOD_REQUIRED);
        }
        this.year = payrollPeriod.year();
        this.month = payrollPeriod.month();
    }
}
