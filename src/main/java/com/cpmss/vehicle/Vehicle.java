package com.cpmss.vehicle;

import com.cpmss.common.BaseEntity;
import com.cpmss.company.Company;
import com.cpmss.department.Department;
import com.cpmss.person.Person;
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

/**
 * Core entity representing a vehicle registered in the compound.
 *
 * <p>Owner is exactly one of: a {@link Person}, a {@link Department},
 * or a {@link Company}. Mutual exclusion is enforced in
 * {@link VehicleRules} and by a CHECK constraint in V2.
 */
@Entity
@Table(name = "Vehicle")
@AttributeOverride(name = "id", column = @Column(name = "vehicle_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    /** License plate number — the real-world identifier. */
    @Column(name = "license_no", nullable = false, unique = true, length = 20)
    private String licenseNo;

    /** Vehicle model description. */
    @Column(name = "vehicle_model", length = 100)
    private String vehicleModel;

    /** Person owner (mutually exclusive with department and company). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_person_id")
    private Person ownerPerson;

    /** Department owner (mutually exclusive with person and company). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_department_id")
    private Department ownerDepartment;

    /** Company owner (mutually exclusive with person and department). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_company_id")
    private Company ownerCompany;
}
