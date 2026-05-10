package com.cpmss.organization.personsupervision;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationAccessRules;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.personsupervision.dto.CreatePersonSupervisionRequest;
import com.cpmss.organization.personsupervision.dto.EndPersonSupervisionRequest;
import com.cpmss.organization.personsupervision.dto.PersonSupervisionResponse;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Orchestrates supervisor-to-staff relationship operations.
 *
 * <p>HR owns mutation. Department managers and supervisors receive only
 * service-filtered read visibility over rows they own through department or
 * direct supervision scope.
 */
@Service
public class PersonSupervisionService {

    private static final Logger log = LoggerFactory.getLogger(PersonSupervisionService.class);

    private final PersonSupervisionRepository repository;
    private final PersonRepository personRepository;
    private final DepartmentScopeService departmentScopeService;
    private final CurrentUserService currentUserService;
    private final OrganizationAccessRules accessRules = new OrganizationAccessRules();
    private final PersonSupervisionRules rules = new PersonSupervisionRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository             supervision data access
     * @param personRepository       person data access
     * @param departmentScopeService department/team scope resolver
     * @param currentUserService     current user resolver
     */
    public PersonSupervisionService(
            PersonSupervisionRepository repository,
            PersonRepository personRepository,
            DepartmentScopeService departmentScopeService,
            CurrentUserService currentUserService) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.departmentScopeService = departmentScopeService;
        this.currentUserService = currentUserService;
    }

    /**
     * Creates a supervision relationship.
     *
     * @param request the supervision details
     * @return the created supervision row
     * @throws ApiException if people are missing or the relationship is invalid
     */
    @Transactional
    public PersonSupervisionResponse create(CreatePersonSupervisionRequest request) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        rules.validateNoSelfSupervision(request.supervisorId(), request.superviseeId());

        if (repository.findBySupervisorIdAndSuperviseeIdAndSupervisionStartDate(
                request.supervisorId(), request.superviseeId(), request.supervisionStartDate()).isPresent()) {
            throw new ApiException(OrganizationErrorCode.PERSON_SUPERVISION_DUPLICATE);
        }

        Person supervisor = personRepository.findById(request.supervisorId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.PERSON_SUPERVISION_NOT_FOUND));
        Person supervisee = personRepository.findById(request.superviseeId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.PERSON_SUPERVISION_NOT_FOUND));

        PersonSupervision supervision = new PersonSupervision();
        supervision.setSupervisor(supervisor);
        supervision.setSupervisee(supervisee);
        supervision.setSupervisionStartDate(request.supervisionStartDate());
        supervision.setTeamName(request.teamName());
        supervision = repository.save(supervision);
        log.info("Person supervision created: supervisor={}, supervisee={}, start={}",
                request.supervisorId(), request.superviseeId(), request.supervisionStartDate());
        return toResponse(supervision);
    }

    /**
     * Ends a supervision relationship.
     *
     * @param request the row identity and end date
     * @return the ended supervision row
     * @throws ApiException if the row does not exist
     */
    @Transactional
    public PersonSupervisionResponse end(EndPersonSupervisionRequest request) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        PersonSupervision supervision = repository
                .findBySupervisorIdAndSuperviseeIdAndSupervisionStartDate(
                        request.supervisorId(), request.superviseeId(), request.supervisionStartDate())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.PERSON_SUPERVISION_NOT_FOUND));
        supervision.setSupervisionEndDate(request.supervisionEndDate());
        supervision = repository.save(supervision);
        log.info("Person supervision ended: supervisor={}, supervisee={}, end={}",
                request.supervisorId(), request.superviseeId(), request.supervisionEndDate());
        return toResponse(supervision);
    }

    /**
     * Lists active supervisees under a supervisor, filtered by caller scope.
     *
     * @param supervisorId supervisor person UUID
     * @return active supervision rows the caller may view
     */
    @Transactional(readOnly = true)
    public List<PersonSupervisionResponse> getActiveSupervisees(UUID supervisorId) {
        CurrentUser user = currentUserService.currentUser();
        return repository.findBySupervisorIdAndSupervisionEndDateIsNull(supervisorId)
                .stream()
                .filter(row -> canView(user, row))
                .map(this::toResponse)
                .toList();
    }

    /**
     * Lists active supervisors for a supervisee, filtered by caller scope.
     *
     * @param superviseeId supervisee person UUID
     * @return active supervision rows the caller may view
     */
    @Transactional(readOnly = true)
    public List<PersonSupervisionResponse> getActiveSupervisors(UUID superviseeId) {
        CurrentUser user = currentUserService.currentUser();
        return repository.findBySuperviseeIdAndSupervisionEndDateIsNull(superviseeId)
                .stream()
                .filter(row -> canView(user, row))
                .map(this::toResponse)
                .toList();
    }

    private boolean canView(CurrentUser user, PersonSupervision row) {
        if (hasOrganizationAuthority(user)) {
            return true;
        }
        UUID userPersonId = user.personId();
        UUID supervisorId = row.getSupervisor().getId();
        UUID superviseeId = row.getSupervisee().getId();
        if (userPersonId != null && userPersonId.equals(supervisorId)) {
            return true;
        }
        return departmentScopeService.activeDepartmentForStaff(superviseeId)
                .map(departmentId -> departmentScopeService.managesDepartment(user, departmentId))
                .orElse(false);
    }

    private boolean hasOrganizationAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.HR_OFFICER);
    }

    private PersonSupervisionResponse toResponse(PersonSupervision supervision) {
        return new PersonSupervisionResponse(
                supervision.getSupervisor().getId(),
                supervision.getSupervisee().getId(),
                supervision.getSupervisionStartDate(),
                supervision.getSupervisionEndDate(),
                supervision.getTeamName());
    }
}
