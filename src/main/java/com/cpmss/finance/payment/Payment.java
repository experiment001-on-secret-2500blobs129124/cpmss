package com.cpmss.finance.payment;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.people.person.Person;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.time.Instant;

/**
 * Central ledger record for any financial transaction in the system.
 *
 * <p>Specialized by {@code payment_type} — exactly one child row
 * (Installment_Payment, Work_Order_Payment, or Payroll_Payment)
 * exists per row. Enforced in {@code PaymentRules}.
 *
 * <p>The amount and currency columns are exposed as a single {@link Money}
 * value object so payment workflows cannot separate amount from currency.
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
    @Convert(converter = PaymentNumberConverter.class)
    @Column(name = "payment_no", nullable = false, unique = true, length = 20)
    private PaymentNumber paymentNo;

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
    @Convert(converter = PaymentTypeConverter.class)
    @Column(name = "payment_type", nullable = false, length = 20)
    @Setter(AccessLevel.NONE)
    private PaymentType paymentType;

    /** Payment method (Cash, Bank Transfer, Cheque, Card, Other). */
    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "method", length = 50)
    @Setter(AccessLevel.NONE)
    private PaymentMethod method;

    /** Direction of payment (Inbound, Outbound). */
    @Convert(converter = PaymentDirectionConverter.class)
    @Column(name = "direction", nullable = false, length = 20)
    @Setter(AccessLevel.NONE)
    private PaymentDirection direction;

    /** External reference number. */
    @Convert(converter = PaymentReferenceConverter.class)
    @Column(name = "reference_no", length = 100)
    private PaymentReference referenceNo;

    /** Reconciliation status (Pending, Reconciled, Disputed). */
    @Convert(converter = ReconciliationStatusConverter.class)
    @Column(name = "reconciliation_status", nullable = false, length = 50)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private ReconciliationStatus reconciliationStatus = ReconciliationStatus.PENDING;

    /** The bank account used for this transaction. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    /** The staff member who processed this payment. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id")
    private Person processedBy;

    /**
     * Returns the payment type label for DTO compatibility.
     *
     * @return the database/API payment type label, or {@code null} when unset
     */
    public String getPaymentType() {
        return paymentType != null ? paymentType.label() : null;
    }

    /**
     * Returns the typed payment discriminator for domain logic.
     *
     * @return the typed payment discriminator, or {@code null} when unset
     */
    public PaymentType getPaymentTypeValue() {
        return paymentType;
    }

    /**
     * Returns the payment method label for DTO compatibility.
     *
     * @return the database/API payment method label, or {@code null} when unset
     */
    public String getMethod() {
        return method != null ? method.label() : null;
    }

    /**
     * Returns the typed payment method for domain logic.
     *
     * @return the typed payment method, or {@code null} when unset
     */
    public PaymentMethod getMethodValue() {
        return method;
    }

    /**
     * Returns the payment direction label for DTO compatibility.
     *
     * @return the database/API payment direction label, or {@code null} when
     *         unset
     */
    public String getDirection() {
        return direction != null ? direction.label() : null;
    }

    /**
     * Returns the typed payment direction for domain logic.
     *
     * @return the typed payment direction, or {@code null} when unset
     */
    public PaymentDirection getDirectionValue() {
        return direction;
    }

    /**
     * Returns the reconciliation status label for DTO compatibility.
     *
     * @return the database/API reconciliation label, or {@code null} when unset
     */
    public String getReconciliationStatus() {
        return reconciliationStatus != null ? reconciliationStatus.label() : null;
    }

    /**
     * Returns the typed reconciliation status for domain logic.
     *
     * @return the typed reconciliation status, or {@code null} when unset
     */
    public ReconciliationStatus getReconciliationStatusValue() {
        return reconciliationStatus;
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

    /**
     * Assigns the typed payment discriminator.
     *
     * @param paymentType the payment discriminator
     * @throws IllegalArgumentException if the payment type is missing
     */
    public void setPaymentType(PaymentType paymentType) {
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type is required");
        }
        this.paymentType = paymentType;
    }

    /**
     * Assigns the optional typed payment method.
     *
     * @param method the optional payment method
     */
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    /**
     * Assigns the typed payment direction.
     *
     * @param direction the payment direction
     * @throws IllegalArgumentException if the direction is missing
     */
    public void setDirection(PaymentDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Payment direction is required");
        }
        this.direction = direction;
    }

    /**
     * Assigns the typed reconciliation status.
     *
     * @param reconciliationStatus the reconciliation status
     * @throws IllegalArgumentException if the status is missing
     */
    public void setReconciliationStatus(ReconciliationStatus reconciliationStatus) {
        if (reconciliationStatus == null) {
            throw new IllegalArgumentException("Reconciliation status is required");
        }
        this.reconciliationStatus = reconciliationStatus;
    }
}
