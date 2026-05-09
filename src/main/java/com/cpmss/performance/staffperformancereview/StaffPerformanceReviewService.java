package com.cpmss.performance.staffperformancereview;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.PerformanceAccessRules;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.staffperformancereview.dto.CreateStaffPerformanceReviewRequest;
import com.cpmss.performance.staffperformancereview.dto.StaffPerformanceReviewResponse;
import com.cpmss.performance.staffperformancereview.dto.UpdateStaffPerformanceReviewRequest;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final CurrentUserService currentUserService;
    private final DepartmentScopeService departmentScopeService;
    private final StaffPerformanceReviewRules rules = new StaffPerformanceReviewRules();
    private final PerformanceAccessRules accessRules = new PerformanceAccessRules();

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
                                         StaffPerformanceReviewMapper mapper,
                                         CurrentUserService currentUserService,
                                         DepartmentScopeService departmentScopeService) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
        this.departmentScopeService = departmentScopeService;
    }

    /**
     * Retrieves a performance review by ID.
     *
     * @param id the performance review UUID
     * @return the matching performance review response
      * @throws ApiException if no review exists with this ID
     */
    @Transactional(readOnly = true)
    public StaffPerformanceReviewResponse getById(UUID id) {
        CurrentUser user = currentUserService.currentUser();
        StaffPerformanceReview review = findOrThrow(id);
        accessRules.requireCanViewStaffPerformance(
                user, review.getStaff().getId(),
                review.getDepartment().getId(), departmentScopeService);
        return mapper.toResponse(review);
    }

    /**
     * Lists performance reviews with pagination.
     *
     * @param pageable the pagination and sorting request
     * @return a paged response of performance reviews
     */
    @Transactional(readOnly = true)
    public PagedResponse<StaffPerformanceReviewResponse> listAll(Pageable pageable) {
        CurrentUser user = currentUserService.currentUser();
        List<StaffPerformanceReviewResponse> content = repository.findAll(pageable).getContent()
                .stream()
                .filter(review -> canViewReview(user, review))
                .map(mapper::toResponse)
                .toList();
        return new PagedResponse<>(
                content, content.size(), 1, pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * Creates a performance review.
     *
     * @param request the performance review creation request
     * @return the created performance review response
      * @throws ApiException if the staff member, reviewer, or department does
      *                      not exist, or the review is invalid
     */
    @Transactional
    public StaffPerformanceReviewResponse create(CreateStaffPerformanceReviewRequest request) {
        CurrentUser user = currentUserService.currentUser();
        accessRules.requireCanCreateReview(
                user, request.reviewerId(), request.departmentId(), departmentScopeService);
        if (!accessRules.isHrOrBusinessAdmin(user)
                && !departmentScopeService.staffBelongsToDepartment(
                        request.staffId(), request.departmentId())) {
            throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
        }
        rules.validateReviewerIsNotSelf(request.staffId(), request.reviewerId());
        PerformanceRating rating = rules.validatePromotionConsistency(
                request.overallRating(),
                request.resultedInPromotion() != null && request.resultedInPromotion(),
                request.resultedInRaise() != null && request.resultedInRaise());
        KpiScore overallScore = KpiScore.nullable(request.overallKpiScore());

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        Person reviewer = personRepository.findById(request.reviewerId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        Department dept = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

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
      * @throws ApiException if no review exists with this ID or the rating or
      *                      score is invalid
     */
    @Transactional
    public StaffPerformanceReviewResponse update(UUID id, UpdateStaffPerformanceReviewRequest request) {
        CurrentUser user = currentUserService.currentUser();
        StaffPerformanceReview review = findOrThrow(id);
        accessRules.requireCanManageDepartment(
                user, review.getDepartment().getId(), departmentScopeService);
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

    private boolean canViewReview(CurrentUser user, StaffPerformanceReview review) {
        try {
            accessRules.requireCanViewStaffPerformance(
                    user, review.getStaff().getId(),
                    review.getDepartment().getId(), departmentScopeService);
            return true;
        } catch (ApiException ex) {
            return false;
        }
    }

    private StaffPerformanceReview findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(PerformanceErrorCode.REVIEW_NOT_FOUND));
    }
}
