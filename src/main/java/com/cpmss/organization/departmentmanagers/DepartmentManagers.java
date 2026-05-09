package com.cpmss.organization.departmentmanagers;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.people.person.Person;
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
 * Junction entity tracking department manager assignments over time.
 *
 * <p>Composite PK: ({@code department_id}, {@code manager_id},
 * {@code management_start_date}). A row with
 * {@code management_end_date IS NULL} represents the current manager.
 */
@Entity
@Table(name = "Department_Managers")
@IdClass(DepartmentManagersId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentManagers extends BaseAuditEntity {

    /** The department being managed (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** The person acting as manager (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Person manager;

    /** The date this management assignment started (part of composite PK). */
    @Id
    @Column(name = "management_start_date", nullable = false)
    private LocalDate managementStartDate;

    /** The date this management assignment ended ({@code null} = still active). */
    @Column(name = "management_end_date")
    private LocalDate managementEndDate;
}
