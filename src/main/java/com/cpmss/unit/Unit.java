package com.cpmss.unit;

import com.cpmss.building.Building;
import com.cpmss.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "unit")
public class Unit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @NotBlank
    @Column(name = "unit_number", nullable = false)
    private String unitNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "bedrooms")
    private Integer bedrooms = 0;

    @Column(name = "bathrooms")
    private Integer bathrooms = 0;

    @Column(name = "rooms")
    private Integer rooms = 0;

    @Column(name = "square_footage")
    private BigDecimal squareFootage;

    @Column(name = "balconies")
    private Integer balconies = 0;

    @Column(name = "view_orientation")
    private String viewOrientation;

    @Column(name = "current_status", nullable = false)
    private String currentStatus = "Vacant";

    @Column(name = "listing_price")
    private BigDecimal listingPrice;

    @Column(name = "water_meter_code")
    private String waterMeterCode;

    @Column(name = "gas_meter_code")
    private String gasMeterCode;

    @Column(name = "electricity_meter_code")
    private String electricityMeterCode;

    /**
     * Derived: total number of rooms.
     */
    @Transient
    public int getTotalRooms() {
        return (bedrooms != null ? bedrooms : 0)
             + (bathrooms != null ? bathrooms : 0)
             + (rooms != null ? rooms : 0);
    }
}
