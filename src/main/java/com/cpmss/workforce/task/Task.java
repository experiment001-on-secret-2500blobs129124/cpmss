package com.cpmss.workforce.task;

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
 * Catalog entity defining task types available in the system.
 *
 * <p>Scoped to a {@link Department} — e.g. "Security Patrol" → Security,
 * "Payroll Processing" → Finance. The department FK here eliminates the
 * need for department_id on {@code Assigned_Task} (avoids 3NF violation).
 */
@Entity
@Table(name = "Task")
@AttributeOverride(name = "id", column = @Column(name = "task_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    /** Short description of the task type. */
    @Column(name = "task_title", nullable = false, length = 50)
    private String taskTitle;

    /** The department this task belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
