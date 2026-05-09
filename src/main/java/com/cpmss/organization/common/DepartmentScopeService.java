package com.cpmss.organization.common;

import com.cpmss.hr.staffpositionhistory.StaffPositionHistory;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.departmentmanagers.DepartmentManagers;
import com.cpmss.organization.departmentmanagers.DepartmentManagersRepository;
import com.cpmss.organization.personsupervision.PersonSupervision;
import com.cpmss.organization.personsupervision.PersonSupervisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Resolves department and team ownership for service-level authorization.
 *
 * <p>This helper does not decide which bounded context owns an action. It only
 * answers relationship questions that multiple staff-operation contexts need:
 * active department manager assignment, active staff department, and active
 * supervisor-to-supervisee links.
 */
@Service
public class DepartmentScopeService {

    private final DepartmentManagersRepository departmentManagersRepository;
    private final PersonSupervisionRepository personSupervisionRepository;
    private final StaffPositionHistoryRepository staffPositionHistoryRepository;

    /**
     * Constructs the scope helper with the relationship repositories it reads.
     *
     * @param departmentManagersRepository manager assignments
     * @param personSupervisionRepository  supervisor assignments
     * @param staffPositionHistoryRepository staff department history
     */
    public DepartmentScopeService(
            DepartmentManagersRepository departmentManagersRepository,
            PersonSupervisionRepository personSupervisionRepository,
            StaffPositionHistoryRepository staffPositionHistoryRepository) {
        this.departmentManagersRepository = departmentManagersRepository;
        this.personSupervisionRepository = personSupervisionRepository;
        this.staffPositionHistoryRepository = staffPositionHistoryRepository;
    }

    /**
     * Checks whether the user has full business visibility.
     *
     * @param user current authenticated user
     * @return true for ADMIN and GENERAL_MANAGER
     */
    public boolean isBusinessAdmin(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN) || user.hasRole(SystemRole.GENERAL_MANAGER);
    }

    /**
     * Checks whether the user is assigned as active manager for a department.
     *
     * @param user         current authenticated user
     * @param departmentId department UUID
     * @return true when a current Department_Managers row links them
     */
    @Transactional(readOnly = true)
    public boolean managesDepartment(CurrentUser user, UUID departmentId) {
        if (user.personId() == null || departmentId == null) {
            return false;
        }
        return isActiveManagerOfDepartment(user.personId(), departmentId);
    }

    /**
     * Checks whether the person is actively assigned as manager of a department.
     *
     * @param personId     manager person UUID
     * @param departmentId department UUID
     * @return true for an active manager row
     */
    @Transactional(readOnly = true)
    public boolean isActiveManagerOfDepartment(UUID personId, UUID departmentId) {
        if (personId == null || departmentId == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return departmentManagersRepository
                .findByDepartmentIdOrderByManagementStartDateDesc(departmentId)
                .stream()
                .filter(row -> isActive(row.getManagementStartDate(), row.getManagementEndDate(), today))
                .map(DepartmentManagers::getManager)
                .anyMatch(manager -> manager != null && personId.equals(manager.getId()));
    }

    /**
     * Checks whether the user actively supervises a staff member.
     *
     * @param user    current authenticated user
     * @param staffId staff person UUID
     * @return true when an active Person_Supervision row links them
     */
    @Transactional(readOnly = true)
    public boolean supervises(CurrentUser user, UUID staffId) {
        if (user.personId() == null || staffId == null) {
            return false;
        }
        return isActiveSupervisorOf(user.personId(), staffId);
    }

    /**
     * Checks whether one person actively supervises another.
     *
     * @param supervisorId supervisor person UUID
     * @param superviseeId supervisee person UUID
     * @return true for an active supervision row
     */
    @Transactional(readOnly = true)
    public boolean isActiveSupervisorOf(UUID supervisorId, UUID superviseeId) {
        if (supervisorId == null || superviseeId == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return personSupervisionRepository.findBySupervisorId(supervisorId).stream()
                .filter(row -> isActive(row.getSupervisionStartDate(), row.getSupervisionEndDate(), today))
                .map(PersonSupervision::getSupervisee)
                .anyMatch(supervisee -> supervisee != null && superviseeId.equals(supervisee.getId()));
    }

    /**
     * Resolves a staff member's active department from Staff_Position_History.
     *
     * @param staffId staff person UUID
     * @return current department UUID, if one can be inferred
     */
    @Transactional(readOnly = true)
    public Optional<UUID> activeDepartmentForStaff(UUID staffId) {
        if (staffId == null) {
            return Optional.empty();
        }
        LocalDate today = LocalDate.now();
        return staffPositionHistoryRepository.findByPersonIdOrderByEffectiveDateDesc(staffId)
                .stream()
                .filter(row -> isActive(row.getEffectiveDate(), row.getEndDate(), today))
                .map(StaffPositionHistory::getPosition)
                .filter(position -> position != null && position.getDepartment() != null)
                .map(position -> position.getDepartment().getId())
                .findFirst();
    }

    /**
     * Checks whether a staff member currently belongs to a department.
     *
     * @param staffId      staff person UUID
     * @param departmentId department UUID
     * @return true when the active staff position belongs to the department
     */
    @Transactional(readOnly = true)
    public boolean staffBelongsToDepartment(UUID staffId, UUID departmentId) {
        return activeDepartmentForStaff(staffId)
                .map(activeDepartmentId -> activeDepartmentId.equals(departmentId))
                .orElse(false);
    }

    private boolean isActive(LocalDate start, LocalDate end, LocalDate today) {
        return (start == null || !start.isAfter(today))
                && (end == null || !end.isBefore(today));
    }
}
