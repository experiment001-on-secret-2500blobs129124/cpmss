package com.cpmss.maintenance.workorder;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.property.facility.Facility;
import com.cpmss.property.facility.FacilityRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.maintenance.workorder.dto.CreateWorkOrderRequest;
import com.cpmss.maintenance.workorder.dto.UpdateWorkOrderRequest;
import com.cpmss.maintenance.workorder.dto.WorkOrderResponse;
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
                            WorkOrderMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.facilityRepository = facilityRepository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a work order by its unique identifier.
     *
     * @param id the work order's UUID primary key
     * @return the matching work order response
     * @throws ResourceNotFoundException if no work order exists with this ID
     */
    @Transactional(readOnly = true)
    public WorkOrderResponse getById(UUID id) {
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
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new work order.
     *
     * @param request the work order details
     * @return the created work order response
     */
    @Transactional
    public WorkOrderResponse create(CreateWorkOrderRequest request) {
        rules.validateCostPositive(request.cost());

        Person requester = personRepository.findById(request.requesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.requesterId()));

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
     * @throws ResourceNotFoundException if no work order exists with this ID
     */
    @Transactional
    public WorkOrderResponse update(UUID id, UpdateWorkOrderRequest request) {
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
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", id));
    }

    private Facility resolveFacility(UUID id) {
        if (id == null) {
            return null;
        }
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
    }

    private Company resolveCompany(UUID id) {
        if (id == null) {
            return null;
        }
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }
}
