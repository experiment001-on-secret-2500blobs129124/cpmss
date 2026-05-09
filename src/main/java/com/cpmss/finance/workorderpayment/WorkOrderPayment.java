package com.cpmss.finance.workorderpayment;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.finance.payment.Payment;
import com.cpmss.maintenance.workorder.WorkOrder;
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
 * Detail entity (1:1 extension of {@link Payment}) for vendor work order payments.
 *
 * <p>PK = {@code payment_id} (shared with Payment).
 */
@Entity
@Table(name = "Work_Order_Payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderPayment extends BaseAuditEntity {

    /** Shared primary key — same as the payment's UUID. */
    @Id
    @Column(name = "payment_id", nullable = false)
    private UUID id;

    /** The parent payment record (1:1 relationship). */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    /** The work order this payment covers. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    /** Vendor invoice number. */
    @Column(name = "invoice_no", length = 100)
    private String invoiceNo;
}
