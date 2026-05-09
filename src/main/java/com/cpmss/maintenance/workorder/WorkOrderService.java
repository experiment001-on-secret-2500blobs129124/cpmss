package com.cpmss.maintenance.workorder;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.maintenance.common.MaintenanceAccessRules;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.property.facility.Facility;
import com.cpmss.property.facility.FacilityRepository;
import com.cpmss.property.common.PropertyErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.maintenance.workorder.dto.CreateWorkOrderRequest;
import com.cpmss.maintenance.workorder.dto.UpdateWorkOrderRequest;
import com.cpmss.maintenance.workorder.dto.WorkOrderResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates work order lifecycle operations.
 *
 * <p>Work orders are permanent records — closed by status change
 * (Completed, Cancelled), never deleted.
 *
 * @see WorkOrderRepository
 */
@Service
public class WorkOrderService {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);

    private final WorkOrderRepository repository;
    private final PersonRepository personRepository;
    private final FacilityRepository facilityRepository;
    private final CompanyRepository companyRepository;
    private final WorkOrderMapper mapper;
    private final CurrentUserService currentUserService;
    private final MaintenanceAccessRules accessRules = new MaintenanceAccessRules();
    private final WorkOrderRules rules = new WorkOrderRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         work order data access
     * @param personRepository   person data access (requester FK)
     * @param facilityRepository facility data access (FK lookup)
     * @param companyRepository  company data access (FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public WorkOrderService(WorkOrderRepository repository,
                            PersonRepository personRepository,
                            FacilityRepository facilityRepository,
                            CompanyRepository companyRepository,
                            WorkOrderMapper mapper,
                            CurrentUserService currentUserService) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.facilityRepository = facilityRepository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a work order by its unique identifier.
     *
     * @param id the work order's UUID primary key
     * @return the matching work order response
        * @throws ApiException if no work order exists with this ID
     */
    @Transactional(readOnly = true)
    public WorkOrderResponse getById(UUID id) {
        accessRules.requireMaintenanceReader(currentUserService.currentUser());
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists all work orders with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of work order DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<WorkOrderResponse> listAll(Pageable pageable) {
        accessRules.requireMaintenanceReader(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new work order.
     *
     * @param request the work order details
     * @return the created work order response
        * @throws ApiException if requester, facility, or company does not exist
     */
    @Transactional
    public WorkOrderResponse create(CreateWorkOrderRequest request) {
        accessRules.requireMaintenanceAdministrator(currentUserService.currentUser());
        rules.validateCostPositive(request.cost());

        Person requester = personRepository.findById(request.requesterId())
            .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));

        WorkOrder workOrder = WorkOrder.builder()
                .workOrderNo(request.workOrderNo())
                .schedule(request.schedule())
                .cost(request.cost())
                .jobStatus(request.jobStatus())
                .description(request.description())
                .priority(request.priority())
                .serviceCategory(request.serviceCategory())
                .requester(requester)
                .facility(resolveFacility(request.facilityId()))
                .company(resolveCompany(request.companyId()))
                .build();
        workOrder = repository.save(workOrder);
        log.info("Work order created: {}", workOrder.getWorkOrderNo());
        return mapper.toResponse(workOrder);
    }

    /**
     * Updates an existing work order.
     *
     * @param id      the work order's UUID
     * @param request the updated values
     * @return the updated work order response
    * @throws ApiException if no work order exists with this ID
     */
    @Transactional
    public WorkOrderResponse update(UUID id, UpdateWorkOrderRequest request) {
        accessRules.requireMaintenanceAdministrator(currentUserService.currentUser());
        WorkOrder workOrder = findOrThrow(id);

        rules.validateCostPositive(request.cost());
        rules.validateStatusTransition(workOrder.getJobStatus(), request.jobStatus());

        workOrder.setSchedule(request.schedule());
        workOrder.setCost(request.cost());
        workOrder.setJobStatus(request.jobStatus());
        workOrder.setDescription(request.description());
        workOrder.setPriority(request.priority());
        workOrder.setServiceCategory(request.serviceCategory());
        workOrder.setFacility(resolveFacility(request.facilityId()));
        workOrder.setCompany(resolveCompany(request.companyId()));
        workOrder = repository.save(workOrder);
        log.info("Work order updated: {}", workOrder.getWorkOrderNo());
        return mapper.toResponse(workOrder);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private WorkOrder findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.WORK_ORDER_NOT_FOUND));
    }

    private Facility resolveFacility(UUID id) {
        if (id == null) {
            return null;
        }
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.FACILITY_NOT_FOUND));
    }

    private Company resolveCompany(UUID id) {
        if (id == null) {
            return null;
        }
        return companyRepository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.COMPANY_NOT_FOUND));
    }
}
