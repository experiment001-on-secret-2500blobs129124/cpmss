package com.cpmss.payment;

import com.cpmss.bankaccount.BankAccount;
import com.cpmss.common.BaseImmutableEntity;
import com.cpmss.installment.Installment;
import com.cpmss.person.Person;
import com.cpmss.workorder.WorkOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payment is immutable — once created and reconciled, it cannot be modified.
 * Corrections require a reversal payment + new payment.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment extends BaseImmutableEntity {

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "method")
    private String method;

    @NotNull
    @Column(name = "direction", nullable = false)
    private String direction;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "reconciliation_status", nullable = false)
    private String reconciliationStatus = "Pending";

    @Column(name = "currency")
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    // --- Polymorphic routing: nullable FKs ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installment_id")
    private Installment installment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;
}
