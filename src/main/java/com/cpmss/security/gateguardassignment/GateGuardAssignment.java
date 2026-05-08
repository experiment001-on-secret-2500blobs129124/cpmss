package com.cpmss.security.gateguardassignment;

import com.cpmss.workforce.assignedtask.AssignedTask;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.security.gate.Gate;
import com.cpmss.people.person.Person;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
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

import java.time.Instant;

/**
 * Junction entity recording which guard was posted at which gate during a shift.
 *
 * <p>{@code task_assignment_id} is NOT NULL — HR must create the
 * {@link AssignedTask} record first, then register the gate posting.
 * {@code shift_end IS NULL} = shift still active.
 */
@Entity
@Table(name = "Gate_Guard_Assignment")
@AttributeOverride(name = "id", column = @Column(name = "guard_post_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateGuardAssignment extends BaseEntity {

    /** The guard posted at this gate. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guard_id", nullable = false)
    private Person guard;

    /** The gate this guard is posted at. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gate_id", nullable = false)
    private Gate gate;

    /** The task assignment that authorizes this posting. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_assignment_id", nullable = false)
    private AssignedTask taskAssignment;

    /** The shift type for this posting. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id")
    private ShiftAttendanceType shiftType;

    /** Shift start timestamp. */
    @Column(name = "shift_start", nullable = false)
    private Instant shiftStart;

    /** Shift end timestamp ({@code null} = still on duty). */
    @Column(name = "shift_end")
    private Instant shiftEnd;
}
