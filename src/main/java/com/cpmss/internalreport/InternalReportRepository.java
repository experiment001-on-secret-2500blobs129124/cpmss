package com.cpmss.internalreport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link InternalReport} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support
 * the pool model (find by assigned role) and notification counts.
 */
public interface InternalReportRepository extends JpaRepository<InternalReport, UUID> {

    /**
     * Find all reports assigned to a specific system role.
     *
     * @param assignedToRole the target role (e.g. "HR_OFFICER")
     * @return reports for that role, ordered by creation date (newest first via default)
     */
    List<InternalReport> findByAssignedToRoleOrderByCreatedAtDesc(String assignedToRole);

    /**
     * Find all reports filed by a specific person.
     *
     * @param reporterId the reporter's person ID
     * @return the reporter's own reports
     */
    List<InternalReport> findByReporterIdOrderByCreatedAtDesc(UUID reporterId);

    /**
     * Count unread reports for a role (notification badge).
     *
     * @param assignedToRole the target role
     * @return number of unread reports
     */
    long countByAssignedToRoleAndIsReadFalse(String assignedToRole);
}
