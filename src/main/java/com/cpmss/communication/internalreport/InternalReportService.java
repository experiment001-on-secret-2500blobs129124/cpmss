package com.cpmss.communication.internalreport;

import com.cpmss.communication.common.CommunicationErrorCode;
import com.cpmss.communication.internalreport.dto.CreateInternalReportRequest;
import com.cpmss.communication.internalreport.dto.InternalReportResponse;
import com.cpmss.communication.internalreport.dto.UpdateInternalReportRequest;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
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
    private final CurrentUserService currentUserService;
    private final InternalReportMapper mapper;
    private final InternalReportRules rules = new InternalReportRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         internal report data access
     * @param personRepository   person data access (FK lookup)
     * @param currentUserService current-user resolver for ownership checks
     * @param mapper             entity-DTO mapper
     */
    public InternalReportService(InternalReportRepository repository,
                                 PersonRepository personRepository,
                                 CurrentUserService currentUserService,
                                 InternalReportMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.currentUserService = currentUserService;
        this.mapper = mapper;
    }

    /**
     * Retrieves a report by its unique identifier.
     *
     * @param id the report's UUID
     * @return the matching report response
     * @throws ApiException if not found or access is denied
     */
    @Transactional(readOnly = true)
    public InternalReportResponse getById(UUID id) {
        InternalReport report = findOrThrow(id);
        rules.validateCanView(currentUserService.currentUser(), report);
        return mapper.toResponse(report);
    }

    /**
     * Lists reports with pagination.
     *
     * <p>Business admins receive all reports. Report receiver roles receive
     * their assigned queue. Other staff receive only reports they filed.
     *
     * @param pageable pagination parameters
     * @return a paged response of report DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<InternalReportResponse> listAll(Pageable pageable) {
        CurrentUser currentUser = currentUserService.currentUser();
        if (rules.isBusinessAdmin(currentUser)) {
            return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
        }
        if (rules.canReceiveReports(currentUser.systemRole())) {
            return PagedResponse.from(repository.findByAssignedToRole(currentUser.systemRole(), pageable),
                    mapper::toResponse);
        }
        UUID reporterId = currentUser.requirePersonId("Viewing internal reports");
        return PagedResponse.from(repository.findByReporterId(reporterId, pageable), mapper::toResponse);
    }

    /**
     * Lists reports by assigned role (pool model).
     *
     * @param assignedToRole the target system role
     * @return reports for that role, newest first
     */
    @Transactional(readOnly = true)
    public List<InternalReportResponse> listByRole(SystemRole assignedToRole) {
        CurrentUser currentUser = currentUserService.currentUser();
        rules.validateCanAccessRoleQueue(currentUser, assignedToRole);
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
        CurrentUser currentUser = currentUserService.currentUser();
        rules.validateCanAccessReporter(currentUser, reporterId);
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
    public long countUnreadByRole(SystemRole assignedToRole) {
        CurrentUser currentUser = currentUserService.currentUser();
        rules.validateCanAccessRoleQueue(currentUser, assignedToRole);
        return repository.countByAssignedToRoleAndIsReadFalse(assignedToRole);
    }

    /**
     * Files a new internal report.
     *
     * @param request the report details
     * @return the created report response
     * @throws ApiException if the reporter does not exist or the inputs are invalid
     */
    @Transactional
    public InternalReportResponse create(CreateInternalReportRequest request) {
        CurrentUser currentUser = currentUserService.currentUser();
        rules.validateCurrentPerson(currentUser, request.reporterId(), "Filing internal reports");
        rules.validateAssignedToRole(request.assignedToRole());
        rules.validateCategory(request.reportCategory());

        Person reporter = personRepository.findById(request.reporterId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));

        InternalReport report = InternalReport.builder()
                .reporter(reporter)
                .assignedToRole(request.assignedToRole())
                .subject(request.subject())
                .body(request.body())
                .reportCategory(request.reportCategory())
                .priority(request.priority() != null ? request.priority() : ReportPriority.NORMAL)
                .reportStatus(ReportStatus.OPEN)
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
     * @throws ApiException if the report or resolver does not exist
     */
    @Transactional
    public InternalReportResponse update(UUID id, UpdateInternalReportRequest request) {
        CurrentUser currentUser = currentUserService.currentUser();
        InternalReport report = findOrThrow(id);
        rules.validateCanProcess(currentUser, report);

        report.setReportStatus(request.reportStatus());
        report.setResolutionNote(request.resolutionNote());

        if (request.resolvedById() != null) {
            rules.validateCurrentPerson(currentUser, request.resolvedById(), "Updating internal reports");
            Person resolver = personRepository.findById(request.resolvedById())
                    .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
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
     * @throws ApiException if the report or reader does not exist
     */
    @Transactional
    public InternalReportResponse markAsRead(UUID id, UUID readBy) {
        CurrentUser currentUser = currentUserService.currentUser();
        InternalReport report = findOrThrow(id);
        rules.validateCanProcess(currentUser, report);
        rules.validateCurrentPerson(currentUser, readBy, "Reading internal reports");
        Person reader = personRepository.findById(readBy)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
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
     * @throws ApiException if the report does not exist
     */
    @Transactional
    public InternalReportResponse markAsUnread(UUID id) {
        CurrentUser currentUser = currentUserService.currentUser();
        InternalReport report = findOrThrow(id);
        rules.validateCanProcess(currentUser, report);
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
     * @throws ApiException if the report or resolver does not exist
     */
    @Transactional
    public InternalReportResponse resolve(UUID id, UUID resolvedById, String resolutionNote) {
        CurrentUser currentUser = currentUserService.currentUser();
        InternalReport report = findOrThrow(id);
        rules.validateCanProcess(currentUser, report);
        rules.validateCurrentPerson(currentUser, resolvedById, "Resolving internal reports");
        Person resolver = personRepository.findById(resolvedById)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        report.setReportStatus(ReportStatus.RESOLVED);
        report.setResolvedBy(resolver);
        report.setResolvedAt(OffsetDateTime.now());
        report.setResolutionNote(resolutionNote);
        report = repository.save(report);
        log.info("Internal report resolved: {} by {}", id, resolvedById);
        return mapper.toResponse(report);
    }

    private InternalReport findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(CommunicationErrorCode.REPORT_NOT_FOUND));
    }
}
