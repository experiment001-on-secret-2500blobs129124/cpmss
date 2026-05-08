package com.cpmss.people.qualification;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ResourceNotFoundException;
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
    private final QualificationRules rules = new QualificationRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository qualification data access
     * @param mapper     entity-DTO mapper
     */
    public QualificationService(QualificationRepository repository, QualificationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a qualification by its unique identifier.
     *
     * @param id the qualification's UUID primary key
     * @return the matching qualification response
     * @throws ResourceNotFoundException if no qualification exists with this ID
     */
    @Transactional(readOnly = true)
    public QualificationResponse getById(UUID id) {
        Qualification q = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification", id));
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
     * @throws ResourceNotFoundException if no qualification exists with this ID
     */
    @Transactional
    public QualificationResponse update(UUID id, UpdateQualificationRequest request) {
        Qualification q = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification", id));
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
     * @throws ResourceNotFoundException if no qualification exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Qualification q = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification", id));
        repository.delete(q);
        log.info("Qualification deleted: {}", q.getQualificationName());
    }
}
