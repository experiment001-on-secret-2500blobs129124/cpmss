package com.cpmss.property.compound;

import com.cpmss.platform.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Root entity representing a managed compound.
 *
 * <p>All buildings, gates, and organizational structures belong
 * to exactly one compound.
 */
@Entity
@Table(name = "Compound")
@AttributeOverride(name = "id", column = @Column(name = "compound_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compound extends BaseEntity {

    /** Display name of the compound. */
    @Column(name = "compound_name", nullable = false, length = 100)
    private String compoundName;

    /** Country where the compound is located. */
    @Column(nullable = false, length = 50)
    private String country;

    /** City where the compound is located. */
    @Column(nullable = false, length = 50)
    private String city;

    /** Optional district or neighbourhood. */
    @Column(length = 50)
    private String district;
}
