package com.cpmss.facility;

import com.cpmss.building.Building;
import com.cpmss.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "facility")
public class Facility extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @NotBlank
    @Column(name = "facility_name", nullable = false)
    private String facilityName;

    @Column(name = "management_type")
    private String managementType;

    @Column(name = "facility_category")
    private String facilityCategory;
}
