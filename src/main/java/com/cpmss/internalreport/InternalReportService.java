package com.cpmss.internalreport;

import com.cpmss.common.PagedResponse;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.internalreport.dto.CreateInternalReportRequest;
import com.cpmss.internalreport.dto.InternalReportResponse;
import com.cpmss.internalreport.dto.UpdateInternalReportRequest;
import com.cpmss.person.Person;
import com.cpmss.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Orchestrates internal report operations.
 *
 * <p>Reports are never deleted — closed by status change.
 * The pool model routes reports to system role groups.
 *
 * @see InternalReportRepository
 */
@Service
public class InternalReportService {

    private static final Logger log = LoggerFactory.getLogger(InternalReportService.class);

    private final InternalReportRepository repository;
    private final PersonRepository personRepository;
    private final InternalReportMapper mapper;

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository       internal report data access
     * @param personRepository person data access (FK lookup)
     * @param mapper           entity-DTO mapper
     */
    public InternalReportService(InternalReportRepository repository,
                                  PersonRepository personRepository,
                                  InternalReportMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a report by its unique identifier.
     *
     * @param id the report's UUID
     * @return the matching report response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public InternalReportResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists all reports with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of report DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<InternalReportResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Files a new internal report.
     *
     * @param request the report details
     * @return the created report response
     */
    @Transactional
    public InternalReportResponse create(CreateInternalReportRequest request) {
        Person reporter = personRepository.findById(request.reporterId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.reporterId()));

        InternalReport report = InternalReport.builder()
                .reporter(reporter)
                .assignedToRole(request.assignedToRole())
                .subject(request.subject())
                .body(request.body())
                .reportCategory(request.reportCategory())
                .priority(request.priority() != null ? request.priority() : "Normal")
                .reportStatus("Open")
                .isRead(false)
                .build();
        report = repository.save(report);
        log.info("Internal report filed: {} assigned to {}", report.getId(), request.assignedToRole());
        return mapper.toResponse(report);
    }

    /**
     * Updates an internal report (status change, resolution).
     *
     * @param id      the report's UUID
     * @param request the update request
     * @return the updated report response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public InternalReportResponse update(UUID id, UpdateInternalReportRequest request) {
        InternalReport report = findOrThrow(id);

        report.setReportStatus(request.reportStatus());
        report.setResolutionNote(request.resolutionNote());

        if (request.resolvedById() != null) {
            Person resolver = personRepository.findById(request.resolvedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Person", request.resolvedById()));
            report.setResolvedBy(resolver);
            report.setResolvedAt(OffsetDateTime.now());
        }

        report = repository.save(report);
        log.info("Internal report updated: {} status={}", report.getId(), request.reportStatus());
        return mapper.toResponse(report);
    }

    private InternalReport findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InternalReport", id));
    }
}
