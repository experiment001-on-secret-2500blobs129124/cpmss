package com.cpmss.people.qualification;

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
 * Catalog entity defining qualification levels.
 *
 * <p>Examples: High School Diploma, Bachelor's Degree, PhD.
 * Referenced by {@code Staff_Profile}.
 */
@Entity
@Table(name = "Qualification")
@AttributeOverride(name = "id", column = @Column(name = "qualification_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Qualification extends BaseEntity {

    /** Human-readable qualification name (unique across the system). */
    @Column(name = "qualification_name", nullable = false, unique = true, length = 100)
    private String qualificationName;
}
