package com.cpmss.maintenance.workorder;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.maintenance.company.Company;
import com.cpmss.property.facility.Facility;
import com.cpmss.people.person.Person;
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
 * Owned entity representing a maintenance or service request raised by a person.
 *
 * <p>Work orders are permanent records. Job status tracks the lifecycle
 * (Open, In Progress, Completed, Cancelled).
 */
@Entity
@Table(name = "Work_Order")
@AttributeOverride(name = "id", column = @Column(name = "work_order_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder extends BaseEntity {

    /** System-unique work order number. */
    @Column(name = "work_order_no", nullable = false, unique = true, length = 20)
    private String workOrderNo;

    /** Scheduled date for the work. */
    @Column(name = "date_scheduled")
    private LocalDate dateScheduled;

    /** Date the work was completed. */
    @Column(name = "date_completed")
    private LocalDate dateCompleted;

    /** Cost of the work. */
    @Column(name = "cost_amount", precision = 12, scale = 2)
    private BigDecimal costAmount;

    /** Job lifecycle status (Open, In Progress, Completed, Cancelled). */
    @Column(name = "job_status", nullable = false, length = 50)
    private String jobStatus;

    /** Detailed description of the work. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Priority level (Low, Medium, High, Critical). */
    @Column(name = "priority", length = 20)
    private String priority;

    /** Service category (Plumbing, Electrical, HVAC, General). */
    @Column(name = "service_category", length = 50)
    private String serviceCategory;

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
