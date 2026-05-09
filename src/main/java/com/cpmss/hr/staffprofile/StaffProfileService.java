package com.cpmss.hr.staffprofile;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.people.qualification.Qualification;
import com.cpmss.people.qualification.QualificationRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.hr.staffprofile.dto.CreateStaffProfileRequest;
import com.cpmss.hr.staffprofile.dto.StaffProfileResponse;
import com.cpmss.hr.staffprofile.dto.UpdateStaffProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates staff profile lifecycle operations.
 *
 * <p>Manages 1:1 extensions of {@link Person} for staff-specific
 * attributes. A staff profile is created when a person is assigned
 * the Staff role. Delegates business rules to {@link StaffProfileRules}.
 *
 * @see StaffProfileRules
 * @see StaffProfileRepository
 */
@Service
public class StaffProfileService {

    private static final Logger log = LoggerFactory.getLogger(StaffProfileService.class);

    private final StaffProfileRepository repository;
    private final PersonRepository personRepository;
    private final QualificationRepository qualificationRepository;
    private final StaffProfileMapper mapper;
    private final CurrentUserService currentUserService;
    private final StaffProfileRules rules = new StaffProfileRules();
    private final HrAccessRules accessRules = new HrAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository                staff profile data access
     * @param personRepository          person data access (FK lookup)
     * @param qualificationRepository   qualification data access (FK lookup)
     * @param mapper                    entity-DTO mapper
     */
    public StaffProfileService(StaffProfileRepository repository,
                               PersonRepository personRepository,
                               QualificationRepository qualificationRepository,
                               StaffProfileMapper mapper,
                               CurrentUserService currentUserService) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.qualificationRepository = qualificationRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a staff profile by the person's UUID.
     *
     * @param personId the person's UUID (also the profile PK)
     * @return the matching staff profile response
     * @throws ApiException if no profile exists for this person
     */
    @Transactional(readOnly = true)
    public StaffProfileResponse getById(UUID personId) {
        accessRules.requireCanViewStaffProfile(currentUserService.currentUser(), personId);
        return mapper.toResponse(repository.findById(personId)
                .orElseThrow(() -> new ApiException(HrErrorCode.STAFF_PROFILE_NOT_FOUND)));
    }

    /**
     * Lists all staff profiles with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of staff profile DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<StaffProfileResponse> listAll(Pageable pageable) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new staff profile for a person.
     *
     * <p>Validates that no profile already exists for this person,
     * and resolves the person and qualification FK references.
     *
     * @param request the create request with person ID and qualification details
     * @return the created staff profile response
     * @throws ApiException if the profile already exists or if the person or
     *                      qualification does not exist
     */
    @Transactional
    public StaffProfileResponse create(CreateStaffProfileRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        rules.validateProfileNotExists(request.personId(),
                repository.existsById(request.personId()));

        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));

        Qualification qualification = qualificationRepository.findById(request.qualificationId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.QUALIFICATION_NOT_FOUND));

        StaffProfile profile = StaffProfile.builder()
                .person(person)
                .qualification(qualification)
                .qualificationDate(request.qualificationDate())
                .cvFileUrl(request.cvFileUrl())
                .build();
        profile = repository.save(profile);
        log.info("StaffProfile created for person: {}", request.personId());
        return mapper.toResponse(profile);
    }

    /**
     * Updates an existing staff profile.
     *
     * @param personId the person's UUID (profile PK)
     * @param request  the update request with new qualification details
     * @return the updated staff profile response
     * @throws ApiException if the profile or qualification does not exist
     */
    @Transactional
    public StaffProfileResponse update(UUID personId, UpdateStaffProfileRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        StaffProfile profile = repository.findById(personId)
                .orElseThrow(() -> new ApiException(HrErrorCode.STAFF_PROFILE_NOT_FOUND));

        Qualification qualification = qualificationRepository.findById(request.qualificationId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.QUALIFICATION_NOT_FOUND));

        profile.setQualification(qualification);
        profile.setQualificationDate(request.qualificationDate());
        profile.setCvFileUrl(request.cvFileUrl());
        profile = repository.save(profile);
        log.info("StaffProfile updated for person: {}", personId);
        return mapper.toResponse(profile);
    }

    /**
     * Deletes a staff profile by person ID.
     *
     * @param personId the person's UUID (profile PK)
     * @throws ApiException if no profile exists for this person
     */
    @Transactional
    public void delete(UUID personId) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        StaffProfile profile = repository.findById(personId)
                .orElseThrow(() -> new ApiException(HrErrorCode.STAFF_PROFILE_NOT_FOUND));
        repository.delete(profile);
        log.info("StaffProfile deleted for person: {}", personId);
    }
}
