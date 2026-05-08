package com.cpmss.performance.staffperformancereview;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.staffperformancereview.dto.CreateStaffPerformanceReviewRequest;
import com.cpmss.performance.staffperformancereview.dto.StaffPerformanceReviewResponse;
import com.cpmss.performance.staffperformancereview.dto.UpdateStaffPerformanceReviewRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates staff performance review operations.
 *
 * <p>Request and response DTOs keep primitive score and rating fields, while
 * this service converts them into {@link KpiScore} and
 * {@link PerformanceRating} before persisting the review entity.
 *
 * @see StaffPerformanceReviewRules
 */
@Service
public class StaffPerformanceReviewService {

    private static final Logger log = LoggerFactory.getLogger(StaffPerformanceReviewService.class);

    private final StaffPerformanceReviewRepository repository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffPerformanceReviewMapper mapper;
    private final StaffPerformanceReviewRules rules = new StaffPerformanceReviewRules();

    /**
     * Creates the performance review service.
     *
     * @param repository repository for review rows
     * @param personRepository repository used to resolve staff and reviewers
     * @param departmentRepository repository used to resolve departments
     * @param mapper mapper used to expose primitive DTO values
     */
    public StaffPerformanceReviewService(StaffPerformanceReviewRepository repository,
                                         PersonRepository personRepository,
                                         DepartmentRepository departmentRepository,
                                         StaffPerformanceReviewMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a performance review by ID.
     *
     * @param id the performance review UUID
     * @return the matching performance review response
     * @throws ResourceNotFoundException if no review exists with this ID
     */
    @Transactional(readOnly = true)
    public StaffPerformanceReviewResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists performance reviews with pagination.
     *
     * @param pageable the pagination and sorting request
     * @return a paged response of performance reviews
     */
    @Transactional(readOnly = true)
    public PagedResponse<StaffPerformanceReviewResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a performance review.
     *
     * @param request the performance review creation request
     * @return the created performance review response
     * @throws ResourceNotFoundException if the staff member, reviewer, or
     *                                   department does not exist
     * @throws com.cpmss.platform.exception.BusinessException if the rating or
     *                                                        score is invalid
     * @throws com.cpmss.platform.exception.ForbiddenException if the staff
     *                                                         member reviews
     *                                                         themselves
     */
    @Transactional
    public StaffPerformanceReviewResponse create(CreateStaffPerformanceReviewRequest request) {
        rules.validateReviewerIsNotSelf(request.staffId(), request.reviewerId());
        PerformanceRating rating = rules.validatePromotionConsistency(
                request.overallRating(),
                request.resultedInPromotion() != null && request.resultedInPromotion(),
                request.resultedInRaise() != null && request.resultedInRaise());
        KpiScore overallScore = KpiScore.nullable(request.overallKpiScore());

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        Person reviewer = personRepository.findById(request.reviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.reviewerId()));
        Department dept = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        StaffPerformanceReview review = StaffPerformanceReview.builder()
                .staff(staff)
                .reviewer(reviewer)
                .department(dept)
                .reviewDate(request.reviewDate())
                .overallKpiScore(overallScore)
                .overallRating(rating)
                .notes(request.notes())
                .resultedInPromotion(request.resultedInPromotion() != null ? request.resultedInPromotion() : false)
                .resultedInRaise(request.resultedInRaise() != null ? request.resultedInRaise() : false)
                .build();
        review = repository.save(review);
        log.info("Performance review created: {} for staff {}", review.getId(), request.staffId());
        return mapper.toResponse(review);
    }

    /**
     * Updates a performance review.
     *
     * @param id the performance review UUID
     * @param request the replacement review values
     * @return the updated performance review response
     * @throws ResourceNotFoundException if no review exists with this ID
     * @throws com.cpmss.platform.exception.BusinessException if the rating or
     *                                                        score is invalid
     */
    @Transactional
    public StaffPerformanceReviewResponse update(UUID id, UpdateStaffPerformanceReviewRequest request) {
        StaffPerformanceReview review = findOrThrow(id);
        PerformanceRating rating = rules.validatePromotionConsistency(
                request.overallRating(),
                request.resultedInPromotion() != null && request.resultedInPromotion(),
                request.resultedInRaise() != null && request.resultedInRaise());
        review.setOverallKpiScore(KpiScore.nullable(request.overallKpiScore()));
        review.setOverallRating(rating);
        review.setNotes(request.notes());
        review.setResultedInPromotion(request.resultedInPromotion() != null ? request.resultedInPromotion() : false);
        review.setResultedInRaise(request.resultedInRaise() != null ? request.resultedInRaise() : false);
        review = repository.save(review);
        log.info("Performance review updated: {}", review.getId());
        return mapper.toResponse(review);
    }

    private StaffPerformanceReview findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerformanceReview", id));
    }
}
