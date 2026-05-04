package com.cpmss.assignedtask;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link AssignedTask} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface AssignedTaskRepository extends JpaRepository<AssignedTask, UUID> {
}
