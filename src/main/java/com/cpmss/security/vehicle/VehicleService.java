package com.cpmss.security.vehicle;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityAccessRules;
import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.security.vehicle.dto.CreateVehicleRequest;
import com.cpmss.security.vehicle.dto.UpdateVehicleRequest;
import com.cpmss.security.vehicle.dto.VehicleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates vehicle lifecycle operations.
 *
 * <p>Vehicles are owned by exactly one of: a {@link Person}, a
 * {@link Department}, or a {@link Company}. The mutual exclusion
 * constraint is enforced by {@link VehicleRules}.
 *
 * @see VehicleRules
 * @see VehicleRepository
 */
@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository repository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;
    private final VehicleMapper mapper;
    private final VehicleRules rules = new VehicleRules();
    private final SecurityAccessRules accessRules = new SecurityAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository           vehicle data access
     * @param personRepository     person data access (owner FK lookup)
     * @param departmentRepository department data access (owner FK lookup)
     * @param companyRepository    company data access (owner FK lookup)
     * @param currentUserService   current-user resolver for ownership checks
     * @param mapper               entity-DTO mapper
     */
    public VehicleService(VehicleRepository repository,
                          PersonRepository personRepository,
                          DepartmentRepository departmentRepository,
                          CompanyRepository companyRepository,
                          CurrentUserService currentUserService,
                          VehicleMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.companyRepository = companyRepository;
        this.currentUserService = currentUserService;
        this.mapper = mapper;
    }

    /**
     * Retrieves a vehicle by its unique identifier.
     *
     * @param id the vehicle's UUID primary key
     * @return the matching vehicle response
     * @throws ApiException if no vehicle exists with this ID
     */
    @Transactional(readOnly = true)
    public VehicleResponse getById(UUID id) {
        accessRules.validateSecurityAdministrator(currentUserService.currentUser());
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.VEHICLE_NOT_FOUND)));
    }

    /**
     * Lists all vehicles with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of vehicle DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<VehicleResponse> listAll(Pageable pageable) {
        accessRules.validateSecurityAdministrator(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new vehicle with exactly one owner.
     *
     * <p>Validates mutual exclusion (exactly one owner FK) and
     * license number uniqueness before saving.
     *
     * @param request the create request with vehicle details and owner ID
     * @return the created vehicle response
    * @throws ApiException if the owner rule is violated or the license is duplicate
     */
    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        accessRules.validateSecurityAdministrator(currentUserService.currentUser());
        rules.validateExactlyOneOwner(
                request.ownerPersonId(), request.ownerDepartmentId(), request.ownerCompanyId());
        LicensePlate licenseNo = LicensePlate.of(request.licenseNo());
        rules.validateLicenseNoUnique(licenseNo.value(),
                repository.existsByLicenseNo(licenseNo));

        Vehicle vehicle = Vehicle.builder()
                .licenseNo(licenseNo)
                .vehicleModel(request.vehicleModel())
                .ownerPerson(resolveOwnerPerson(request.ownerPersonId()))
                .ownerDepartment(resolveOwnerDepartment(request.ownerDepartmentId()))
                .ownerCompany(resolveOwnerCompany(request.ownerCompanyId()))
                .build();
        vehicle = repository.save(vehicle);
        log.info("Vehicle created: {}", vehicle.getLicenseNo());
        return mapper.toResponse(vehicle);
    }

    /**
     * Updates an existing vehicle.
     *
     * @param id      the vehicle's UUID
     * @param request the update request with new values
     * @return the updated vehicle response
     * @throws ApiException if no vehicle exists with this ID
     */
    @Transactional
    public VehicleResponse update(UUID id, UpdateVehicleRequest request) {
        accessRules.validateSecurityAdministrator(currentUserService.currentUser());
        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.VEHICLE_NOT_FOUND));

        rules.validateExactlyOneOwner(
                request.ownerPersonId(), request.ownerDepartmentId(), request.ownerCompanyId());
        LicensePlate licenseNo = LicensePlate.of(request.licenseNo());

        if (!vehicle.getLicenseNo().equals(licenseNo.value())) {
            rules.validateLicenseNoUnique(licenseNo.value(),
                    repository.existsByLicenseNo(licenseNo));
        }

        vehicle.setLicenseNo(licenseNo);
        vehicle.setVehicleModel(request.vehicleModel());
        vehicle.setOwnerPerson(resolveOwnerPerson(request.ownerPersonId()));
        vehicle.setOwnerDepartment(resolveOwnerDepartment(request.ownerDepartmentId()));
        vehicle.setOwnerCompany(resolveOwnerCompany(request.ownerCompanyId()));
        vehicle = repository.save(vehicle);
        log.info("Vehicle updated: {}", vehicle.getLicenseNo());
        return mapper.toResponse(vehicle);
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param id the vehicle's UUID
     * @throws ApiException if no vehicle exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.validateSecurityAdministrator(currentUserService.currentUser());
        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.VEHICLE_NOT_FOUND));
        repository.delete(vehicle);
        log.info("Vehicle deleted: {}", vehicle.getLicenseNo());
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Person resolveOwnerPerson(UUID id) {
        if (id == null) {
            return null;
        }
        return personRepository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
    }

    private Department resolveOwnerDepartment(UUID id) {
        if (id == null) {
            return null;
        }
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
    }

    private Company resolveOwnerCompany(UUID id) {
        if (id == null) {
            return null;
        }
        return companyRepository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.COMPANY_NOT_FOUND));
    }
}
