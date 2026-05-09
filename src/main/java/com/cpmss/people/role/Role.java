package com.cpmss.people.role;

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
 * Catalog entity defining person roles in the compound.
 *
 * <p>Examples: Staff, Tenant, Investor, Visitor.
 * A person can hold multiple roles simultaneously via the
 * {@code Person_Role} junction table.
 */
@Entity
@Table(name = "Role")
@AttributeOverride(name = "id", column = @Column(name = "role_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    /** Human-readable role name (unique across the system). */
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;
}
