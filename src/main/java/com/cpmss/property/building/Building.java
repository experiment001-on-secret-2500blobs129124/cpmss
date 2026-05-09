package com.cpmss.property.building;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.property.common.BuildingType;
import com.cpmss.property.common.BuildingTypeConverter;
import com.cpmss.property.common.NonNegativeCount;
import com.cpmss.property.common.NonNegativeCountConverter;
import com.cpmss.property.compound.Compound;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.time.LocalDate;

/**
 * Owned entity representing a physical building within a compound.
 *
 * <p>Units and facilities belong to a building. A building cannot
 * exist without a compound — the FK uses {@code ON DELETE CASCADE}.
 */
@Entity
@Table(name = "Building")
@AttributeOverride(name = "id", column = @Column(name = "building_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building extends BaseEntity {

    /** Building number within the compound. */
    @Column(name = "building_no", nullable = false, length = 20)
    private String buildingNo;

    /** Optional display name. */
    @Column(name = "building_name", length = 100)
    private String buildingName;

    /** Building classification (e.g. Residential, Commercial). */
    @Convert(converter = BuildingTypeConverter.class)
    @Column(name = "building_type", length = 50)
    private BuildingType buildingType;

    /** Number of floors in the building. */
    @Convert(converter = NonNegativeCountConverter.class)
    @Column(name = "floors_count")
    private NonNegativeCount floorsCount;

    /** Date when construction was completed. */
    @Column(name = "construction_date")
    private LocalDate constructionDate;

    /** The compound this building belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private Compound compound;
}
