package com.cpmss.leasing.installment;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.InstallmentStatus;
import com.cpmss.leasing.common.InstallmentStatusConverter;
import com.cpmss.leasing.common.InstallmentType;
import com.cpmss.leasing.common.InstallmentTypeConverter;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.leasing.contract.Contract;
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

import java.time.LocalDate;

/**
 * Child entity representing a single payment installment owed under a contract.
 *
 * <p>Installments are permanent financial records — they cannot be
 * deleted and belong to exactly one {@link Contract}.
 */
@Entity
@Table(name = "Installment")
@AttributeOverride(name = "id", column = @Column(name = "installment_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Installment extends BaseEntity {

    /** Installment category (Rent, Deposit, Penalty, Other). */
    @Convert(converter = InstallmentTypeConverter.class)
    @Column(name = "installment_type", nullable = false, length = 50)
    @Setter(AccessLevel.NONE)
    private InstallmentType installmentType;

    /** When this installment is due. */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /** Lifecycle status (Pending, Partially Paid, Paid, Overdue, Cancelled). */
    @Convert(converter = InstallmentStatusConverter.class)
    @Column(name = "installment_status", nullable = false, length = 50)
    @Setter(AccessLevel.NONE)
    private InstallmentStatus installmentStatus;

    /** Expected payment money with explicit currency. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "amount_expected", nullable = false, precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "amount_expected_currency", nullable = false, length = 10))
    })
    @Setter(AccessLevel.NONE)
    private Money amountExpected;

    /** The contract this installment belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * Assigns the typed installment category.
     *
     * @param installmentType the installment category
     * @throws IllegalArgumentException if the type is missing
     */
    public void setInstallmentType(InstallmentType installmentType) {
        if (installmentType == null) {
            throw new IllegalArgumentException("Installment type is required");
        }
        this.installmentType = installmentType;
    }

    /**
     * Assigns the typed installment status.
     *
     * @param installmentStatus the installment status
     * @throws IllegalArgumentException if the status is missing
     */
    public void setInstallmentStatus(InstallmentStatus installmentStatus) {
        if (installmentStatus == null) {
            throw new IllegalArgumentException("Installment status is required");
        }
        this.installmentStatus = installmentStatus;
    }

    /**
     * Assigns a strictly positive expected installment amount.
     *
     * @param amountExpected expected installment money
     * @throws IllegalArgumentException if the money is missing or not positive
     */
    public void setAmountExpected(Money amountExpected) {
        if (amountExpected == null) {
            throw new IllegalArgumentException("Installment amount is required");
        }
        if (amountExpected.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Installment amount must be positive");
        }
        this.amountExpected = amountExpected;
    }
}
