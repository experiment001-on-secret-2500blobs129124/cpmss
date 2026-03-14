package com.cpmss.building;

import com.cpmss.common.BaseEntity;
import com.cpmss.compound.Compound;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "building")
public class Building extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private Compound compound;

    @NotBlank
    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Column(name = "building_number")
    private String buildingNumber;

    @NotBlank
    @Column(name = "building_type", nullable = false)
    private String buildingType;

    @Column(name = "floors_count")
    private Integer floorsCount;

    @Column(name = "construction_date")
    private java.time.LocalDate constructionDate;
}
