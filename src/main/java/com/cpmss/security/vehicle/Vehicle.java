package com.cpmss.security.vehicle;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.maintenance.company.Company;
import com.cpmss.organization.department.Department;
import com.cpmss.people.person.Person;
import com.cpmss.security.accesspermit.AccessPermit;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Core entity representing a vehicle registered in the compound.
 *
 * <p>Owner is exactly one of: a {@link Person}, a {@link Department},
 * or a {@link Company}. Mutual exclusion is enforced in
 * {@link VehicleRules} and by a CHECK constraint in V2.
 */
@Entity
@Table(name = "Vehicle")
@AttributeOverride(name = "id", column = @Column(name = "vehicle_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    /** License plate number — the real-world identifier. */
    @Convert(converter = LicensePlateConverter.class)
    @Column(name = "license_no", nullable = false, unique = true, length = 20)
    @Setter(lombok.AccessLevel.NONE)
    private LicensePlate licenseNo;

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

    /** Access permits linked to this vehicle. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Vehicle_Permits",
            joinColumns = @JoinColumn(name = "vehicle_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "permit_id", nullable = false))
    @Builder.Default
    private Set<AccessPermit> permits = new HashSet<>();

    /**
     * Returns the license plate string for DTO compatibility.
     *
     * @return the normalized license plate value, or {@code null} when unset
     */
    public String getLicenseNo() {
        return licenseNo != null ? licenseNo.value() : null;
    }

    /**
     * Returns the typed license plate for domain logic.
     *
     * @return the typed license plate, or {@code null} when unset
     */
    public LicensePlate getLicenseNoValue() {
        return licenseNo;
    }

    /**
     * Assigns the typed license plate.
     *
     * @param licenseNo the typed license plate
     * @throws IllegalArgumentException if the license plate is missing
     */
    public void setLicenseNo(LicensePlate licenseNo) {
        if (licenseNo == null) {
            throw new IllegalArgumentException("License plate is required");
        }
        this.licenseNo = licenseNo;
    }
}
