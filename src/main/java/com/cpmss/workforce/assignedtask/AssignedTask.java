package com.cpmss.workforce.assignedtask;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.people.person.Person;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import com.cpmss.workforce.task.Task;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Junction entity representing a daily duty assignment for a staff member.
 *
 * <p>Records which task and shift type a staff member is scheduled for
 * each day. Uses surrogate PK so {@code Gate_Guard_Assignment} can FK
 * to this record for gate-type duties.
 */
@Entity
@Table(name = "Assigned_Task", uniqueConstraints =
        @UniqueConstraint(columnNames = {"staff_id", "task_id", "assignment_date"}))
@AttributeOverride(name = "id", column = @Column(name = "assignment_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedTask extends BaseEntity {

    /** The staff member assigned. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Person staff;

    /** The task type. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /** The shift type for this assignment. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftAttendanceType shift;

    /** The date of the assignment. */
    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    /** Optional description of the specific duty. */
    @Column(name = "duty_description", length = 200)
    private String dutyDescription;
}
