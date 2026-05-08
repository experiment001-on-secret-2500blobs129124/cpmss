package com.cpmss.property.facility;

import com.cpmss.property.building.Building;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.maintenance.company.Company;
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
 * Core entity representing a shared facility within a building.
 *
 * <p>Facilities (gyms, pools, parking) may be managed by the
 * compound directly or by an external vendor ({@link Company}).
 * The management model is captured in {@code managementType}
 * ("Compound" or "Vendor") with a matching FK.
 *
 * <p>Operating hours are tracked in a separate SCD-Type-2
 * history table (Phase 4).
 */
@Entity
@Table(name = "Facility")
@AttributeOverride(name = "id", column = @Column(name = "facility_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facility extends BaseEntity {

    /** Facility display name. */
    @Column(name = "facility_name", nullable = false, length = 150)
    private String facilityName;

    /** Facility classification (e.g. Gym, Pool, Parking). */
    @Column(name = "facility_category", length = 50)
    private String facilityCategory;

    /** Management model — "Compound" or "Vendor". */
    @Column(name = "management_type", nullable = false, length = 20)
    private String managementType;

    /** The building this facility is located in. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    /** The external company managing this facility (set when managementType is "Vendor"). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_by_company_id")
    private Company managedByCompany;
}
