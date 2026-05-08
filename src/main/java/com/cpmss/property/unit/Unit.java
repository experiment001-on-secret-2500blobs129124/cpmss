package com.cpmss.property.unit;

import com.cpmss.property.building.Building;
import com.cpmss.platform.common.BaseEntity;
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

/**
 * Owned entity representing a residential or commercial unit within a building.
 *
 * <p>Units are permanent records — physical demolition is reflected by
 * a status change in {@code Unit_Status_History}, never by deletion.
 * Unit number uniqueness within a building is enforced in
 * {@link UnitRules}.
 */
@Entity
@Table(name = "Unit")
@AttributeOverride(name = "id", column = @Column(name = "unit_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends BaseEntity {

    /** Unit number within the building. */
    @Column(name = "unit_no", nullable = false, length = 20)
    private String unitNo;

    /** Floor number where the unit is located. */
    @Column(name = "floor_no")
    private Integer floorNo;

    /** Total number of rooms. */
    @Column(name = "no_of_rooms")
    private Integer noOfRooms;

    /** Number of bathrooms. */
    @Column(name = "no_of_bathrooms")
    private Integer noOfBathrooms;

    /** Number of bedrooms. */
    @Column(name = "no_of_bedrooms")
    private Integer noOfBedrooms;

    /** Total room count (all types combined). */
    @Column(name = "no_of_total_rooms")
    private Integer noOfTotalRooms;

    /** Number of balconies. */
    @Column(name = "no_of_balconies")
    private Integer noOfBalconies;

    /** Area in square feet. */
    @Column(name = "square_foot", precision = 10, scale = 2)
    private BigDecimal squareFoot;

    /** View orientation (e.g. North, Sea, Garden). */
    @Column(name = "view_orientation", length = 50)
    private String viewOrientation;

    /** Gas meter reference code. */
    @Column(name = "gas_meter_code", length = 50)
    private String gasMeterCode;

    /** Water meter reference code. */
    @Column(name = "water_meter_code", length = 50)
    private String waterMeterCode;

    /** Electricity meter reference code. */
    @Column(name = "electricity_meter_code", length = 50)
    private String electricityMeterCode;

    /** The building this unit belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
}
