package com.cpmss.performance.staffperformancereview;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.hr.staffposition.PositionSalaryHistory;
import com.cpmss.hr.staffposition.PositionSalaryHistoryRepository;
import com.cpmss.hr.staffposition.StaffPositionRepository;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistory;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistory;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryRules;
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
    private final StaffPositionRepository staffPositionRepository;
    private final StaffPositionHistoryRepository positionHistoryRepository;
    private final PositionSalaryHistoryRepository positionSalaryHistoryRepository;
    private final StaffSalaryHistoryRepository salaryHistoryRepository;
    private final StaffPerformanceReviewMapper mapper;
    private final CurrentUserService currentUserService;
    private final DepartmentScopeService departmentScopeService;
    private final StaffPerformanceReviewRules rules = new StaffPerformanceReviewRules();
    private final StaffSalaryRules salaryRules = new StaffSalaryRules();
    private final PerformanceAccessRules accessRules = new PerformanceAccessRules();

    /**
     * Creates the performance review service.
     *
     * @param repository repository for review rows
     * @param personRepository repository used to resolve staff and reviewers
     * @param departmentRepository repository used to resolve departments
     * @param staffPositionRepository repository used to resolve promotion positions
     * @param positionHistoryRepository repository used to write promotion history
     * @param positionSalaryHistoryRepository repository used to resolve salary bands
     * @param salaryHistoryRepository repository used to write raise history
     * @param mapper mapper used to expose primitive DTO values
     */
    public StaffPerformanceReviewService(StaffPerformanceReviewRepository repository,
                                         PersonRepository personRepository,
                                         DepartmentRepository departmentRepository,
                                         StaffPositionRepository staffPositionRepository,
                                         StaffPositionHistoryRepository positionHistoryRepository,
                                         PositionSalaryHistoryRepository positionSalaryHistoryRepository,
                                         StaffSalaryHistoryRepository salaryHistoryRepository,
                                         StaffPerformanceReviewMapper mapper,
                                         CurrentUserService currentUserService,
                                         DepartmentScopeService departmentScopeService) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.staffPositionRepository = staffPositionRepository;
        this.positionHistoryRepository = positionHistoryRepository;
        this.positionSalaryHistoryRepository = positionSalaryHistoryRepository;
        this.salaryHistoryRepository = salaryHistoryRepository;
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
                .resultedInPromotion(Boolean.TRUE.equals(request.resultedInPromotion()))
                .resultedInRaise(Boolean.TRUE.equals(request.resultedInRaise()))
                .build();
        review = repository.save(review);
        applyReviewOutcomes(request, review, staff, reviewer);
        log.info("performance_review_created reviewId={} staffId={}",
                review.getId(), request.staffId());
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
    public StaffPerformanceReviewResponse update(UUID id,
                                                 UpdateStaffPerformanceReviewRequest request) {
        CurrentUser user = currentUserService.currentUser();
        StaffPerformanceReview review = findOrThrow(id);
        accessRules.requireCanManageDepartment(
                user, review.getDepartment().getId(), departmentScopeService);
        boolean nextPromotion = request.resultedInPromotion() != null
                ? request.resultedInPromotion()
                : Boolean.TRUE.equals(review.getResultedInPromotion());
        boolean nextRaise = request.resultedInRaise() != null
                ? request.resultedInRaise()
                : Boolean.TRUE.equals(review.getResultedInRaise());
        rules.validateOutcomeFlagsUnchanged(
                Boolean.TRUE.equals(review.getResultedInPromotion()),
                Boolean.TRUE.equals(review.getResultedInRaise()),
                nextPromotion, nextRaise);
        PerformanceRating rating = rules.validatePromotionConsistency(
                request.overallRating(), nextPromotion, nextRaise);
        review.setOverallKpiScore(KpiScore.nullable(request.overallKpiScore()));
        review.setOverallRating(rating);
        review.setNotes(request.notes());
        review.setResultedInPromotion(nextPromotion);
        review.setResultedInRaise(nextRaise);
        review = repository.save(review);
        log.info("performance_review_updated reviewId={}", review.getId());
        return mapper.toResponse(review);
    }

    private void applyReviewOutcomes(CreateStaffPerformanceReviewRequest request,
                                     StaffPerformanceReview review,
                                     Person staff,
                                     Person reviewer) {
        if (Boolean.TRUE.equals(request.resultedInPromotion())) {
            applyPromotion(request, review, staff, reviewer);
        }
        if (Boolean.TRUE.equals(request.resultedInRaise())) {
            applyRaise(request, review, staff, reviewer);
        }
    }

    private void applyPromotion(CreateStaffPerformanceReviewRequest request,
                                StaffPerformanceReview review,
                                Person staff,
                                Person reviewer) {
        if (request.newPositionId() == null) {
            throw new ApiException(PerformanceErrorCode.PROMOTION_POSITION_REQUIRED);
        }
        StaffPosition position = staffPositionRepository.findById(request.newPositionId())
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));
        positionHistoryRepository.findByPersonIdAndEndDateIsNull(staff.getId())
                .ifPresent(current -> {
                    if (!current.getEffectiveDate().isBefore(request.reviewDate())) {
                        throw new ApiException(HrErrorCode.STAFF_POSITION_ASSIGNMENT_OVERLAP);
                    }
                    current.setEndDate(request.reviewDate().minusDays(1));
                    positionHistoryRepository.save(current);
                });

        StaffPositionHistory history = new StaffPositionHistory();
        history.setPerson(staff);
        history.setPosition(position);
        history.setEffectiveDate(request.reviewDate());
        history.setAuthorizedBy(reviewer);
        positionHistoryRepository.save(history);
    }

    private void applyRaise(CreateStaffPerformanceReviewRequest request,
                            StaffPerformanceReview review,
                            Person staff,
                            Person reviewer) {
        if (request.newBaseDailyRate() == null || request.newMaximumSalary() == null) {
            throw new ApiException(PerformanceErrorCode.RAISE_SALARY_REQUIRED);
        }
        salaryRules.validateBaseDailyRatePositive(request.newBaseDailyRate());
        salaryRules.validateMaximumSalaryPositive(request.newMaximumSalary());
        StaffPosition raisePosition = resolveRaisePosition(request, staff);
        PositionSalaryHistory salaryBand = positionSalaryHistoryRepository
                .findFirstByPositionIdAndSalaryEffectiveDateLessThanEqualOrderBySalaryEffectiveDateDesc(
                        raisePosition.getId(), request.reviewDate())
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_SALARY_HISTORY_NOT_FOUND));
        salaryRules.validateWithinPositionMaximum(
                request.newMaximumSalary(), salaryBand.getMaximumSalary());
        if (salaryHistoryRepository.existsByStaffIdAndEffectiveDate(
                staff.getId(), request.reviewDate())) {
            throw new ApiException(HrErrorCode.STAFF_SALARY_HISTORY_DUPLICATE);
        }
        salaryHistoryRepository.findByStaffIdAndEndDateIsNull(staff.getId())
                .ifPresent(current -> {
                    current.setEndDate(request.reviewDate().minusDays(1));
                    salaryHistoryRepository.save(current);
                });

        StaffSalaryHistory history = new StaffSalaryHistory();
        history.setStaff(staff);
        history.setEffectiveDate(request.reviewDate());
        history.setBaseDailyRate(request.newBaseDailyRate());
        history.setMaximumSalary(request.newMaximumSalary());
        history.setApprovedBy(reviewer);
        history.setReviewId(review.getId());
        salaryHistoryRepository.save(history);
    }


    private StaffPosition resolveRaisePosition(CreateStaffPerformanceReviewRequest request,
                                               Person staff) {
        if (Boolean.TRUE.equals(request.resultedInPromotion())) {
            return staffPositionRepository.findById(request.newPositionId())
                    .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));
        }
        return positionHistoryRepository.findByPersonIdAndEndDateIsNull(staff.getId())
                .map(StaffPositionHistory::getPosition)
                .orElseThrow(() -> new ApiException(HrErrorCode.STAFF_POSITION_HISTORY_NOT_FOUND));
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
