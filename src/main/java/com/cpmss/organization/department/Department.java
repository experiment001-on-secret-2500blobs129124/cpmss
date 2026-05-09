package com.cpmss.organization.department;

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
 * Catalog entity defining departments in the compound.
 *
 * <p>Examples: Security, Maintenance, HR, Finance.
 * Referenced by {@code Task}, {@code Staff_Position}, and other
 * department-scoped entities.
 */
@Entity
@Table(name = "Department")
@AttributeOverride(name = "id", column = @Column(name = "department_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity {

    /** Human-readable department name (unique across the system). */
    @Column(name = "department_name", nullable = false, unique = true, length = 100)
    private String departmentName;
}
