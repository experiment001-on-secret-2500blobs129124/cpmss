package com.cpmss.workforce.shiftattendancetype;

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
 * Catalog entity defining shift types used in attendance tracking.
 *
 * <p>Examples: Morning, Night, Rotating.
 * Referenced by {@code Assigned_Task}, {@code Attends}, and
 * {@code Gate_Guard_Assignment}.
 */
@Entity
@Table(name = "Shift_Attendance_Type")
@AttributeOverride(name = "id", column = @Column(name = "shift_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAttendanceType extends BaseEntity {

    /** Human-readable shift name. */
    @Column(name = "shift_name", nullable = false)
    private String shiftName;
}
