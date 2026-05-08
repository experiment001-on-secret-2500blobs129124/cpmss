package com.cpmss.leasing.installment;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.leasing.contract.Contract;
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
    @Column(name = "installment_type", nullable = false, length = 50)
    private String installmentType;

    /** When this installment is due. */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /** Lifecycle status (Pending, Partially Paid, Paid, Overdue, Cancelled). */
    @Column(name = "installment_status", nullable = false, length = 50)
    private String installmentStatus;

    /** Expected payment amount — must be positive. */
    @Column(name = "amount_expected", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountExpected;

    /** The contract this installment belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;
}
