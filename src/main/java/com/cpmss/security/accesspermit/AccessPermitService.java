package com.cpmss.security.accesspermit;

import com.cpmss.security.accesspermit.dto.AccessPermitResponse;
import com.cpmss.security.accesspermit.dto.CreateAccessPermitRequest;
import com.cpmss.security.accesspermit.dto.UpdateAccessPermitRequest;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.leasing.contract.Contract;
import com.cpmss.leasing.contract.ContractRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.hr.staffprofile.StaffProfile;
import com.cpmss.hr.staffprofile.StaffProfileRepository;
import com.cpmss.maintenance.workorder.WorkOrder;
import com.cpmss.maintenance.workorder.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates access permit lifecycle operations.
 *
 * <p>Permits are revoked by status change, never deleted.
 * Exactly one entitlement basis must be set — enforced by
 * {@link AccessPermitRules}.
 *
 * @see AccessPermitRules
 * @see AccessPermitRepository
 */
@Service
public class AccessPermitService {

    private static final Logger log = LoggerFactory.getLogger(AccessPermitService.class);

    private final AccessPermitRepository repository;
    private final PersonRepository personRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final ContractRepository contractRepository;
    private final WorkOrderRepository workOrderRepository;
    private final AccessPermitMapper mapper;
    private final AccessPermitRules rules = new AccessPermitRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository             access permit data access
     * @param personRepository       person data access (holder/issuer FK lookup)
     * @param staffProfileRepository staff profile data access (entitlement FK lookup)
     * @param contractRepository     contract data access (entitlement FK lookup)
     * @param workOrderRepository    work order data access (entitlement FK lookup)
     * @param mapper                 entity-DTO mapper
     */
    public AccessPermitService(AccessPermitRepository repository,
                               PersonRepository personRepository,
                               StaffProfileRepository staffProfileRepository,
                               ContractRepository contractRepository,
                               WorkOrderRepository workOrderRepository,
                               AccessPermitMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.staffProfileRepository = staffProfileRepository;
        this.contractRepository = contractRepository;
        this.workOrderRepository = workOrderRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves an access permit by its unique identifier.
     *
     * @param id the permit's UUID primary key
     * @return the matching permit response
     * @throws ResourceNotFoundException if no permit exists with this ID
     */
    @Transactional(readOnly = true)
    public AccessPermitResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists all access permits with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of permit DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<AccessPermitResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new access permit with entitlement validation.
     *
     * @param request the create request with permit details
     * @return the created permit response
     * @throws com.cpmss.platform.exception.BusinessException if entitlement rule is violated
     */
    @Transactional
    public AccessPermitResponse create(CreateAccessPermitRequest request) {
        rules.validateExactlyOneEntitlement(
                request.staffProfileId(), request.contractId(),
                request.workOrderId(), request.invitedById());

        AccessPermit permit = AccessPermit.builder()
                .permitNo(request.permitNo())
                .permitType(PermitType.fromLabel(request.permitType()))
                .accessLevel(AccessLevel.fromNullableLabel(request.accessLevel()))
                .permitStatus(PermitStatus.fromLabel(request.permitStatus()))
                .validity(new PermitValidity(request.issueDate(), request.expiryDate()))
                .permitHolder(resolvePerson(request.permitHolderId()))
                .staffProfile(resolveStaffProfile(request.staffProfileId()))
                .contract(resolveContract(request.contractId()))
                .workOrder(resolveWorkOrder(request.workOrderId()))
                .invitedBy(resolvePersonNullable(request.invitedById()))
                .issuedBy(resolvePerson(request.issuedById()))
                .build();
        permit = repository.save(permit);
        log.info("Access permit created: {} for holder {}", permit.getPermitNo(),
                request.permitHolderId());
        return mapper.toResponse(permit);
    }

    /**
     * Updates an existing access permit (status, access level, expiry only).
     *
     * @param id      the permit's UUID
     * @param request the update request
     * @return the updated permit response
     * @throws ResourceNotFoundException if no permit exists with this ID
     */
    @Transactional
    public AccessPermitResponse update(UUID id, UpdateAccessPermitRequest request) {
        AccessPermit permit = findOrThrow(id);

        permit.setAccessLevel(AccessLevel.fromNullableLabel(request.accessLevel()));
        permit.setPermitStatus(PermitStatus.fromLabel(request.permitStatus()));
        permit.setValidity(new PermitValidity(permit.getIssueDate(), request.expiryDate()));
        permit = repository.save(permit);
        log.info("Access permit updated: {} status={}", permit.getPermitNo(),
                permit.getPermitStatus());
        return mapper.toResponse(permit);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private AccessPermit findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AccessPermit", id));
    }

    private Person resolvePerson(UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", id));
    }

    private Person resolvePersonNullable(UUID id) {
        if (id == null) {
            return null;
        }
        return resolvePerson(id);
    }

    private StaffProfile resolveStaffProfile(UUID id) {
        if (id == null) {
            return null;
        }
        return staffProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffProfile", id));
    }

    private Contract resolveContract(UUID id) {
        if (id == null) {
            return null;
        }
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", id));
    }

    private WorkOrder resolveWorkOrder(UUID id) {
        if (id == null) {
            return null;
        }
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", id));
    }
}
