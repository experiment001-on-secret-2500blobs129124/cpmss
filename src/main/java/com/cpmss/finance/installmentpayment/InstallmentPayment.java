package com.cpmss.finance.installmentpayment;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.leasing.installment.Installment;
import com.cpmss.finance.payment.Payment;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
 * Detail entity (1:1 extension of {@link Payment}) for resident installment payments.
 *
 * <p>PK = {@code payment_id} (shared with Payment).
 */
@Entity
@Table(name = "Installment_Payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPayment extends BaseAuditEntity {

    /** Shared primary key — same as the payment's UUID. */
    @Id
    @Column(name = "payment_id", nullable = false)
    private UUID id;

    /** The parent payment record (1:1 relationship). */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    /** The installment this payment applies to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installment_id", nullable = false)
    private Installment installment;

    /** Optional late fee money mapped to the late-fee amount and currency columns. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "late_fee_amount", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "late_fee_currency", length = 10))
    })
    private Money lateFee;
}
