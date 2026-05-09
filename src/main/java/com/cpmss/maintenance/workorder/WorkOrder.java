package com.cpmss.maintenance.workorder;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.maintenance.company.Company;
import com.cpmss.property.facility.Facility;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Owned entity representing a maintenance or service request raised by a person.
 *
 * <p>Work orders are permanent records. Job status tracks the lifecycle
 * (Pending, Assigned, In Progress, Completed, Paid, Cancelled).
 */
@Entity
@Table(name = "Work_Order")
@AttributeOverride(name = "id", column = @Column(name = "work_order_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder extends BaseEntity {

    /** System-unique work order number. */
    @Column(name = "work_order_no", nullable = false, unique = true, length = 20)
    private String workOrderNo;

    /** Optional scheduled/completed date pair. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "dateScheduled",
                    column = @Column(name = "date_scheduled")),
            @AttributeOverride(name = "dateCompleted",
                    column = @Column(name = "date_completed"))
    })
    private WorkOrderSchedule schedule;

    /** Cost of the work with explicit currency. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "cost_amount", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "cost_currency", length = 10))
    })
    private Money cost;

    /** Job lifecycle status. */
    @Convert(converter = WorkOrderStatusConverter.class)
    @Column(name = "job_status", nullable = false, length = 50)
    private WorkOrderStatus jobStatus;

    /** Detailed description of the work. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Priority level. */
    @Convert(converter = WorkOrderPriorityConverter.class)
    @Column(name = "priority", length = 20)
    private WorkOrderPriority priority;

    /** Service category. */
    @Convert(converter = ServiceCategoryConverter.class)
    @Column(name = "service_category", length = 50)
    private ServiceCategory serviceCategory;

    /** The person who raised this work order. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Person requester;

    /** The facility this work order relates to ({@code null} = not facility-specific). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    /** The company assigned to perform the work ({@code null} = unassigned). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
