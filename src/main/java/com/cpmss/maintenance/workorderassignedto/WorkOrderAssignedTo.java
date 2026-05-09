package com.cpmss.maintenance.workorderassignedto;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.workorder.WorkOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Junction entity recording which company was assigned to a work order (M:M).
 *
 * <p>Composite PK: ({@code work_order_id}, {@code company_id}).
 * Full {@code @Entity} because the assignment date is tracked as extra data.
 */
@Entity
@Table(name = "Work_Order_Assigned_To")
@IdClass(WorkOrderAssignedToId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderAssignedTo extends BaseAuditEntity {

    /** The work order (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    /** The assigned company (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /** The date the company was assigned. */
    @Column(name = "date_assigned", nullable = false)
    private LocalDate dateAssigned;
}
