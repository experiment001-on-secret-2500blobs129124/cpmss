package com.cpmss.hr.staffposition;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.organization.department.Department;
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
 * Owned entity representing a named job position scoped to a department.
 *
 * <p>Examples: Security Guard, Maintenance Tech, HR Officer.
 * Cannot exist without a {@link Department}.
 */
@Entity
@Table(name = "Staff_Position")
@AttributeOverride(name = "id", column = @Column(name = "position_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPosition extends BaseEntity {

    /** Human-readable position title. */
    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    /** The department this position belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
