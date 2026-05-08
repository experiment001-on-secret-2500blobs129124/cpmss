package com.cpmss.organization.departmentlocationhistory;

import com.cpmss.property.building.Building;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.organization.department.Department;
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
 * SCD Type 2 entity tracking which building a department is located in over time.
 *
 * <p>Composite PK: ({@code department_id}, {@code location_start_date_in_building}).
 * A row with {@code location_end_date_in_building IS NULL} represents
 * the current location.
 */
@Entity
@Table(name = "Department_Location_History")
@IdClass(DepartmentLocationHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentLocationHistory extends BaseAuditEntity {

    /** The department whose location is tracked (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /** The date this location became effective (part of composite PK). */
    @Id
    @Column(name = "location_start_date_in_building")
    private LocalDate locationStartDateInBuilding;

    /** The date this location ended ({@code null} = still current). */
    @Column(name = "location_end_date_in_building")
    private LocalDate locationEndDateInBuilding;

    /** The building where the department is/was located. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
}
