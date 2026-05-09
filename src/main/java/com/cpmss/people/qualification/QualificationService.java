package com.cpmss.people.qualification;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.people.common.PeopleAccessRules;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.people.qualification.dto.CreateQualificationRequest;
import com.cpmss.people.qualification.dto.QualificationResponse;
import com.cpmss.people.qualification.dto.UpdateQualificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates qualification lifecycle operations.
 *
 * <p>Handles CRUD for Qualification entities. Delegates business rules
 * to {@link QualificationRules} and data access to {@link QualificationRepository}.
 *
 * @see QualificationRules
 * @see QualificationRepository
 */
@Service
public class QualificationService {

    private static final Logger log = LoggerFactory.getLogger(QualificationService.class);

    private final QualificationRepository repository;
    private final QualificationMapper mapper;
    private final CurrentUserService currentUserService;
    private final QualificationRules rules = new QualificationRules();
    private final PeopleAccessRules accessRules = new PeopleAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository qualification data access
     * @param mapper     entity-DTO mapper
     */
    public QualificationService(QualificationRepository repository, QualificationMapper mapper,
                                CurrentUserService currentUserService) {
        this.repository = repository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a qualification by its unique identifier.
     *
     * @param id the qualification's UUID primary key
     * @return the matching qualification response
     * @throws ApiException if no qualification exists with this ID
     */
    @Transactional(readOnly = true)
    public QualificationResponse getById(UUID id) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Qualification q = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.QUALIFICATION_NOT_FOUND));
        return mapper.toResponse(q);
    }

    /**
     * Lists all qualifications with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of qualification DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<QualificationResponse> listAll(Pageable pageable) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new qualification.
     *
     * @param request the create request with the qualification name
     * @return the created qualification response
     */
    @Transactional
    public QualificationResponse create(CreateQualificationRequest request) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        rules.validateNameUnique(
                request.qualificationName(),
                repository.existsByQualificationName(request.qualificationName()));
        Qualification q = mapper.toEntity(request);
        q = repository.save(q);
        log.info("Qualification created: {}", q.getQualificationName());
        return mapper.toResponse(q);
    }

    /**
     * Updates an existing qualification.
     *
     * @param id      the qualification's UUID
     * @param request the update request with the new name
     * @return the updated qualification response
     * @throws ApiException if no qualification exists with this ID
     */
    @Transactional
    public QualificationResponse update(UUID id, UpdateQualificationRequest request) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Qualification q = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.QUALIFICATION_NOT_FOUND));
        if (!q.getQualificationName().equals(request.qualificationName())) {
            rules.validateNameUnique(
                    request.qualificationName(),
                    repository.existsByQualificationName(request.qualificationName()));
        }
        q.setQualificationName(request.qualificationName());
        q = repository.save(q);
        log.info("Qualification updated: {}", q.getQualificationName());
        return mapper.toResponse(q);
    }

    /**
     * Deletes a qualification by ID.
     *
     * @param id the qualification's UUID
     * @throws ApiException if no qualification exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Qualification q = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.QUALIFICATION_NOT_FOUND));
        repository.delete(q);
        log.info("Qualification deleted: {}", q.getQualificationName());
    }
}
