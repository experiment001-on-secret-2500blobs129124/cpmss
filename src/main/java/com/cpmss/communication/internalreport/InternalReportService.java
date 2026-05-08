package com.cpmss.communication.internalreport;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.communication.internalreport.dto.CreateInternalReportRequest;
import com.cpmss.communication.internalreport.dto.InternalReportResponse;
import com.cpmss.communication.internalreport.dto.UpdateInternalReportRequest;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Orchestrates internal report operations.
 *
 * <p>Reports are never deleted — closed by status change.
 * The pool model routes reports to system role groups.
 *
 * @see InternalReportRules
 * @see InternalReportRepository
 */
@Service
public class InternalReportService {

    private static final Logger log = LoggerFactory.getLogger(InternalReportService.class);

    private final InternalReportRepository repository;
    private final PersonRepository personRepository;
    private final InternalReportMapper mapper;
    private final InternalReportRules rules = new InternalReportRules();

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
     * Lists reports by assigned role (pool model).
     *
     * @param assignedToRole the target system role
     * @return reports for that role, newest first
     */
    @Transactional(readOnly = true)
    public List<InternalReportResponse> listByRole(String assignedToRole) {
        return repository.findByAssignedToRoleOrderByCreatedAtDesc(assignedToRole)
                .stream().map(mapper::toResponse).toList();
    }

    /**
     * Lists reports filed by a specific person.
     *
     * @param reporterId the reporter's person UUID
     * @return the reporter's own reports, newest first
     */
    @Transactional(readOnly = true)
    public List<InternalReportResponse> listByReporter(UUID reporterId) {
        return repository.findByReporterIdOrderByCreatedAtDesc(reporterId)
                .stream().map(mapper::toResponse).toList();
    }

    /**
     * Counts unread reports for a role (notification badge).
     *
     * @param assignedToRole the target system role
     * @return number of unread reports
     */
    @Transactional(readOnly = true)
    public long countUnreadByRole(String assignedToRole) {
        return repository.countByAssignedToRoleAndIsReadFalse(assignedToRole);
    }

    /**
     * Files a new internal report.
     *
     * @param request the report details
     * @return the created report response
     */
    @Transactional
    public InternalReportResponse create(CreateInternalReportRequest request) {
        rules.validateAssignedToRole(request.assignedToRole());
        rules.validateCategory(request.reportCategory());

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

    /**
     * Marks a report as read.
     *
     * @param id     the report's UUID
     * @param readBy the person marking as read
     * @return the updated report response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public InternalReportResponse markAsRead(UUID id, UUID readBy) {
        InternalReport report = findOrThrow(id);
        Person reader = personRepository.findById(readBy)
                .orElseThrow(() -> new ResourceNotFoundException("Person", readBy));
        report.setRead(true);
        report.setReadAt(OffsetDateTime.now());
        report.setReadBy(reader);
        report = repository.save(report);
        log.info("Internal report marked as read: {} by {}", id, readBy);
        return mapper.toResponse(report);
    }

    /**
     * Marks a report as unread.
     *
     * @param id the report's UUID
     * @return the updated report response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public InternalReportResponse markAsUnread(UUID id) {
        InternalReport report = findOrThrow(id);
        report.setRead(false);
        report.setReadAt(null);
        report.setReadBy(null);
        report = repository.save(report);
        log.info("Internal report marked as unread: {}", id);
        return mapper.toResponse(report);
    }

    /**
     * Resolves a report with a resolution note.
     *
     * @param id             the report's UUID
     * @param resolvedById   the person resolving
     * @param resolutionNote the resolution explanation
     * @return the updated report response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public InternalReportResponse resolve(UUID id, UUID resolvedById, String resolutionNote) {
        InternalReport report = findOrThrow(id);
        Person resolver = personRepository.findById(resolvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Person", resolvedById));
        report.setReportStatus("Resolved");
        report.setResolvedBy(resolver);
        report.setResolvedAt(OffsetDateTime.now());
        report.setResolutionNote(resolutionNote);
        report = repository.save(report);
        log.info("Internal report resolved: {} by {}", id, resolvedById);
        return mapper.toResponse(report);
    }

    private InternalReport findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InternalReport", id));
    }
}
