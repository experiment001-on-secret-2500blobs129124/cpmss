package com.cpmss.communication.internalreport;

import com.cpmss.identity.auth.SystemRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link InternalReport} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support
 * the pool model (find by assigned role), reporter ownership, and notification
 * counts.
 */
public interface InternalReportRepository extends JpaRepository<InternalReport, UUID> {

    /**
     * Find all reports assigned to a specific system role.
     *
     * @param assignedToRole the target role
     * @return reports for that role, ordered by creation date (newest first via default)
     */
    List<InternalReport> findByAssignedToRoleOrderByCreatedAtDesc(SystemRole assignedToRole);

    /**
     * Find all reports assigned to a specific system role using pagination.
     *
     * @param assignedToRole the target role
     * @param pageable       pagination parameters
     * @return reports for that role
     */
    Page<InternalReport> findByAssignedToRole(SystemRole assignedToRole, Pageable pageable);

    /**
     * Find all reports filed by a specific person.
     *
     * @param reporterId the reporter's person ID
     * @return the reporter's own reports
     */
    List<InternalReport> findByReporterIdOrderByCreatedAtDesc(UUID reporterId);

    /**
     * Find all reports filed by a specific person using pagination.
     *
     * @param reporterId the reporter's person ID
     * @param pageable   pagination parameters
     * @return the reporter's own reports
     */
    Page<InternalReport> findByReporterId(UUID reporterId, Pageable pageable);

    /**
     * Count unread reports for a role (notification badge).
     *
     * @param assignedToRole the target role
     * @return number of unread reports
     */
    long countByAssignedToRoleAndIsReadFalse(SystemRole assignedToRole);
}
