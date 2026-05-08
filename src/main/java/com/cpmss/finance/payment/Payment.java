package com.cpmss.finance.payment;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.people.person.Person;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
 *
 * <p>The amount and currency columns are exposed as a {@link Money} value
 * object inside the entity. DTOs still expose primitive JSON fields so
 * existing API payloads remain stable while domain code works with a single
 * validated monetary concept.
 *
 * @see Money
 * @see PaymentRules
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

    /** Payment money mapped to the existing amount and currency columns. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "amount", nullable = false, precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "currency", nullable = false, length = 10))
    })
    @Setter(AccessLevel.NONE)
    private Money money;

    /** Payment type discriminator (Installment, WorkOrder, Payroll). */
    @Column(name = "payment_type", nullable = false, length = 20)
    private String paymentType;

    /** Payment method (Cash, Bank Transfer, Cheque, Card, Other). */
    @Column(name = "method", length = 50)
    private String method;

    /** Direction of payment (Inbound, Outbound). */
    @Column(name = "direction", nullable = false, length = 20)
    private String direction;

    /** External reference number. */
    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    /** Reconciliation status (Pending, Reconciled, Disputed). */
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

    /**
     * Returns the payment amount for DTO compatibility.
     *
     * <p>Domain logic should prefer {@link #getMoney()} when both amount and
     * currency are needed together. This method keeps existing response
     * mapping code stable after embedding {@link Money}.
     *
     * @return the payment amount, or {@code null} if the embedded money value
     *         has not been initialized
     */
    public BigDecimal getAmount() {
        return money != null ? money.getAmount() : null;
    }

    /**
     * Returns the payment currency for DTO compatibility.
     *
     * <p>Domain logic should prefer {@link #getMoney()} when both amount and
     * currency are needed together. This method keeps existing response
     * mapping code stable after embedding {@link Money}.
     *
     * @return the ISO-4217 payment currency, or {@code null} if the embedded
     *         money value has not been initialized
     */
    public String getCurrency() {
        return money != null ? money.getCurrency() : null;
    }

    /**
     * Assigns a strictly positive money value to the payment.
     *
     * <p>Payments are ledger movements, so zero-value money is rejected even
     * though the reusable {@link Money} constructor can represent zero for
     * other financial concepts.
     *
     * @param money the validated payment money
     * @throws IllegalArgumentException if the money is missing or not positive
     */
    public void setMoney(Money money) {
        if (money == null) {
            throw new IllegalArgumentException("Payment money is required");
        }
        if (money.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.money = money;
    }
}
