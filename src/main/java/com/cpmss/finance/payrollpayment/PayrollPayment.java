package com.cpmss.finance.payrollpayment;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.finance.payment.Payment;
import com.cpmss.people.person.Person;
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
    @Column(name = "payment_id")
    private UUID id;

    /** The parent payment record (1:1 relationship). */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "payment_id")
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
}
