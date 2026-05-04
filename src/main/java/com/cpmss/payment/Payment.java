package com.cpmss.payment;

import com.cpmss.bankaccount.BankAccount;
import com.cpmss.common.BaseEntity;
import com.cpmss.person.Person;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Central ledger record for any financial transaction in the system.
 *
 * <p>Specialized by {@code payment_type} — exactly one child row
 * (Installment_Payment, Work_Order_Payment, or Payroll_Payment)
 * exists per row. Enforced in {@code PaymentRules}.
 */
@Entity
@Table(name = "Payment")
@AttributeOverride(name = "id", column = @Column(name = "payment_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    /** System-unique payment number. */
    @Column(name = "payment_no", nullable = false, unique = true, length = 20)
    private String paymentNo;

    /** Timestamp of the payment. */
    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    /** Payment amount. */
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** Currency code (default USD). */
    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "USD";

    /** Payment type discriminator (Installment, WorkOrder, Payroll). */
    @Column(name = "payment_type", nullable = false, length = 20)
    private String paymentType;

    /** Payment method (Cash, BankTransfer, Check, Online). */
    @Column(name = "method", length = 50)
    private String method;

    /** Direction of payment (Inbound, Outbound). */
    @Column(name = "direction", nullable = false, length = 20)
    private String direction;

    /** External reference number. */
    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    /** Reconciliation status (Pending, Matched, Unmatched). */
    @Column(name = "reconciliation_status", nullable = false, length = 50)
    @Builder.Default
    private String reconciliationStatus = "Pending";

    /** The bank account used for this transaction. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    /** The staff member who processed this payment. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id")
    private Person processedBy;
}
