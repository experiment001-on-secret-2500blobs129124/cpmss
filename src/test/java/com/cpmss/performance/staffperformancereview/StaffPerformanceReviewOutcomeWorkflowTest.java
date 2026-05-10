package com.cpmss.performance.staffperformancereview;

import com.cpmss.hr.staffposition.StaffPositionRepository;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.staffperformancereview.dto.CreateStaffPerformanceReviewRequest;
import com.cpmss.performance.staffperformancereview.dto.UpdateStaffPerformanceReviewRequest;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffPerformanceReviewOutcomeWorkflowTest {

    @Mock
    private StaffPerformanceReviewRepository reviewRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private StaffPositionRepository staffPositionRepository;

    @Mock
    private StaffPositionHistoryRepository positionHistoryRepository;

    @Mock
    private StaffSalaryHistoryRepository salaryHistoryRepository;

    @Mock
    private StaffPerformanceReviewMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private DepartmentScopeService departmentScopeService;

    @Test
    void promotionOutcomeRequiresTargetPosition() {
        UUID staffId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        Person staff = person(staffId);
        Person reviewer = person(reviewerId);
        Department department = new Department();
        department.setId(departmentId);
        StaffPerformanceReview saved = new StaffPerformanceReview();
        saved.setId(UUID.randomUUID());

        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), reviewerId, SystemRole.HR_OFFICER, "hr@example.com"));
        when(personRepository.findById(staffId)).thenReturn(Optional.of(staff));
        when(personRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(reviewRepository.save(any(StaffPerformanceReview.class))).thenReturn(saved);

        StaffPerformanceReviewService service = service();

        assertThatThrownBy(() -> service.create(new CreateStaffPerformanceReviewRequest(
                staffId,
                reviewerId,
                departmentId,
                LocalDate.of(2026, 5, 1),
                new BigDecimal("88.50"),
                PerformanceRating.GOOD.label(),
                "Ready for promotion",
                true,
                false,
                null,
                null,
                null)))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void updateRejectsOutcomeFlagChangesWithoutOutcomeDetails() {
        UUID reviewId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        StaffPerformanceReview review = new StaffPerformanceReview();
        review.setId(reviewId);
        review.setDepartment(department);
        review.setResultedInPromotion(false);
        review.setResultedInRaise(false);

        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), UUID.randomUUID(), SystemRole.HR_OFFICER, "hr@example.com"));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        StaffPerformanceReviewService service = service();

        assertThatThrownBy(() -> service.update(reviewId, new UpdateStaffPerformanceReviewRequest(
                new BigDecimal("91.00"),
                PerformanceRating.EXCELLENT.label(),
                "Approved raise",
                false,
                true)))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                PerformanceErrorCode.REVIEW_OUTCOME_IMMUTABLE));

        verify(reviewRepository, never()).save(any(StaffPerformanceReview.class));
    }

    private StaffPerformanceReviewService service() {
        return new StaffPerformanceReviewService(
                reviewRepository,
                personRepository,
                departmentRepository,
                staffPositionRepository,
                positionHistoryRepository,
                salaryHistoryRepository,
                mapper,
                currentUserService,
                departmentScopeService);
    }

    private Person person(UUID id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }
}
