package com.cpmss.workorder;

import com.cpmss.common.BaseEntity;
import com.cpmss.company.Company;
import com.cpmss.facility.Facility;
import com.cpmss.person.Person;
import com.cpmss.unit.Unit;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "work_order")
public class WorkOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private Person requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_company_id")
    private Company assignedCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_facility_id")
    private Facility targetFacility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_unit_id")
    private Unit targetUnit;

    @Column(name = "date_scheduled")
    private LocalDate dateScheduled;

    @Column(name = "date_completed")
    private LocalDate dateCompleted;

    @Column(name = "date_assigned")
    private LocalDate dateAssigned;

    @Column(name = "cost_amount")
    private BigDecimal costAmount;

    @Column(name = "job_status", nullable = false)
    private String jobStatus = "Pending";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "priority")
    private String priority = "Normal";

    @Column(name = "service_category")
    private String serviceCategory;
}
