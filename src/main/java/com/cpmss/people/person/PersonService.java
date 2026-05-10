package com.cpmss.people.person;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.people.common.EgyptianNationalId;
import com.cpmss.people.common.Gender;
import com.cpmss.people.common.PassportNumber;
import com.cpmss.people.common.PeopleAccessRules;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.dto.CreatePersonRequest;
import com.cpmss.people.person.dto.PersonResponse;
import com.cpmss.people.person.dto.UpdatePersonRequest;
import com.cpmss.people.role.Role;
import com.cpmss.people.role.RoleRepository;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Orchestrates person lifecycle operations.
 *
 * <p>Person creation is transactional: the person record, their
 * multi-value phone/email collections, and role assignments via
 * {@link PersonRole} are all saved atomically. Business invariants
 * are delegated to {@link PersonRules}.
 *
 * @see PersonRules
 * @see PersonRepository
 * @see PersonRoleRepository
 */
@Service
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository repository;
    private final PersonRoleRepository personRoleRepository;
    private final RoleRepository roleRepository;
    private final CurrentUserService currentUserService;
    private final PersonRules rules = new PersonRules();
    private final PeopleAccessRules accessRules = new PeopleAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository           person data access
     * @param personRoleRepository person-role junction data access
     * @param roleRepository       role catalog data access (for FK lookup)
     */
    public PersonService(PersonRepository repository,
                         PersonRoleRepository personRoleRepository,
                         RoleRepository roleRepository,
                         CurrentUserService currentUserService) {
        this.repository = repository;
        this.personRoleRepository = personRoleRepository;
        this.roleRepository = roleRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a person by their unique identifier.
     *
     * <p>Eagerly loads phone numbers, email addresses, and assigned
     * role names for the response.
     *
     * @param id the person's UUID primary key
     * @return the matching person response
     * @throws ApiException if no person exists with this ID
     */
    @Transactional(readOnly = true)
    public PersonResponse getById(UUID id) {
        accessRules.requireCanViewPerson(currentUserService.currentUser(), id);
        Person person = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        return toResponse(person);
    }

    /**
     * Lists all persons with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of person DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<PersonResponse> listAll(Pageable pageable) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), this::toResponse);
    }

    /**
     * Creates a new person with roles, phone numbers, and emails.
     *
     * <p>Enforces business rules: at least one role, valid gender,
     * Egyptian national ID required for Egyptian nationals, and
     * passport uniqueness. All writes are atomic.
     *
     * @param request the create request with person details and role IDs
     * @return the created person response
     * @throws ApiException if a person business rule is violated
     */
    @Transactional
    public PersonResponse create(CreatePersonRequest request) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        rules.validateAtLeastOneRole(request.roleIds());
        Gender gender = rules.validateGender(request.gender());
        EgyptianNationalId nationalId = rules.validateEgyptianNationalId(
                request.nationality(), request.egyptianNationalId());
        PassportNumber passportNo = PassportNumber.of(request.passportNo());
        rules.validatePassportUnique(passportNo, repository.existsByPassportNo(passportNo));

        Person person = Person.builder()
                .passportNo(passportNo)
                .egyptianNationalId(nationalId)
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .nationality(request.nationality())
                .city(request.city())
                .street(request.street())
                .dateOfBirth(request.dateOfBirth())
                .gender(gender)
                .phones(toPhoneSet(request.phones()))
                .emails(toEmailSet(request.emails()))
                .build();

        person = repository.save(person);
        assignRoles(person, request.roleIds());

        log.info("Person created: {} {}", person.getFirstName(), person.getLastName());
        return toResponse(person);
    }

    /**
     * Updates an existing person.
     *
     * <p>If {@code roleIds} is provided and non-empty, the existing
     * role set is fully replaced. Phone and email collections are
     * also replaced if provided.
     *
     * @param id      the person's UUID
     * @param request the update request with new values
     * @return the updated person response
     * @throws ApiException if no person exists with this ID
     */
    @Transactional
    public PersonResponse update(UUID id, UpdatePersonRequest request) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        accessRules.requireCanUpdatePerson(
                currentUserService.currentUser(), id, isContactOnlyUpdate(person, request));

        Gender gender = rules.validateGender(request.gender());
        EgyptianNationalId nationalId = rules.validateEgyptianNationalId(
                request.nationality(), request.egyptianNationalId());
        PassportNumber passportNo = PassportNumber.of(request.passportNo());

        if (!person.getPassportNo().equals(passportNo.value())) {
            rules.validatePassportUnique(passportNo, repository.existsByPassportNo(passportNo));
        }

        person.setPassportNo(passportNo);
        person.setEgyptianNationalId(nationalId);
        person.setFirstName(request.firstName());
        person.setMiddleName(request.middleName());
        person.setLastName(request.lastName());
        person.setNationality(request.nationality());
        person.setCity(request.city());
        person.setStreet(request.street());
        person.setDateOfBirth(request.dateOfBirth());
        person.setGender(gender);

        if (request.phones() != null) {
            person.getPhones().clear();
            person.getPhones().addAll(toPhoneSet(request.phones()));
        }
        if (request.emails() != null) {
            person.getEmails().clear();
            person.getEmails().addAll(toEmailSet(request.emails()));
        }

        if (request.roleIds() != null && !request.roleIds().isEmpty()) {
            rules.validateAtLeastOneRole(request.roleIds());
            personRoleRepository.deleteByPersonId(person.getId());
            personRoleRepository.flush();
            assignRoles(person, request.roleIds());
        }

        person = repository.save(person);
        log.info("Person updated: {} {}", person.getFirstName(), person.getLastName());
        return toResponse(person);
    }

    /**
     * Deletes a person and all their role assignments.
     *
     * @param id the person's UUID
     * @throws ApiException if no person exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Person person = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        personRoleRepository.deleteByPersonId(id);
        repository.delete(person);
        log.info("Person deleted: {} {}", person.getFirstName(), person.getLastName());
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private boolean isContactOnlyUpdate(Person person, UpdatePersonRequest request) {
        return (request.roleIds() == null || request.roleIds().isEmpty())
                && Objects.equals(person.getPassportNo(), request.passportNo())
                && Objects.equals(person.getEgyptianNationalId(), request.egyptianNationalId())
                && Objects.equals(person.getFirstName(), request.firstName())
                && Objects.equals(person.getMiddleName(), request.middleName())
                && Objects.equals(person.getLastName(), request.lastName())
                && Objects.equals(person.getNationality(), request.nationality())
                && Objects.equals(person.getCity(), request.city())
                && Objects.equals(person.getStreet(), request.street())
                && Objects.equals(person.getDateOfBirth(), request.dateOfBirth())
                && Objects.equals(person.getGender(), request.gender());
    }

    private void assignRoles(Person person, List<UUID> roleIds) {
        for (UUID roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ApiException(PeopleErrorCode.ROLE_NOT_FOUND));
            PersonRole pr = new PersonRole();
            pr.setPerson(person);
            pr.setRole(role);
            personRoleRepository.save(pr);
        }
    }

    private Set<PersonPhone> toPhoneSet(List<CreatePersonRequest.PhoneEntry> phones) {
        if (phones == null) {
            return new HashSet<>();
        }
        return phones.stream()
                .map(p -> new PersonPhone(p.countryCode(), p.phone()))
                .collect(Collectors.toSet());
    }

    private Set<PersonEmail> toEmailSet(List<String> emails) {
        if (emails == null) {
            return new HashSet<>();
        }
        return emails.stream()
                .map(PersonEmail::new)
                .collect(Collectors.toSet());
    }

    private PersonResponse toResponse(Person person) {
        List<PersonRole> roles = personRoleRepository.findByPersonId(person.getId());
        return new PersonResponse(
                person.getId(),
                person.getPassportNo(),
                person.getEgyptianNationalId(),
                person.getFirstName(),
                person.getMiddleName(),
                person.getLastName(),
                person.getNationality(),
                person.getCity(),
                person.getStreet(),
                person.getDateOfBirth(),
                person.getGender(),
                person.getIsBlacklisted(),
                person.getPhones().stream()
                        .map(p -> new PersonResponse.PhoneEntry(p.getCountryCode(), p.getPhone()))
                        .toList(),
                person.getEmails().stream()
                        .map(PersonEmail::getEmail)
                        .toList(),
                roles.stream()
                        .map(pr -> pr.getRole().getRoleName())
                        .toList(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }
}
