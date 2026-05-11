package com.cpmss.performance.kpipolicy;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.kpipolicy.dto.CreateKpiPolicyRequest;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies KPI policy service tier-overlap checks before persistence.
 */
@ExtendWith(MockitoExtension.class)
class KpiPolicyServiceTierOverlapTest {

    @Mock
    private KpiPolicyRepository repository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private KpiPolicyMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void createRejectsOverlappingTierInSameDepartmentPolicyVersion() {
        UUID departmentId = UUID.randomUUID();
        LocalDate effectiveDate = LocalDate.of(2026, 1, 1);
        Department department = new Department();
        department.setId(departmentId);
        KpiPolicy existing = KpiPolicy.builder()
                .department(department)
                .effectiveDate(effectiveDate)
                .tierLabel(PerformanceRating.GOOD)
                .minKpiScore(KpiScore.of(new BigDecimal("40.00")))
                .maxKpiScore(KpiScore.of(new BigDecimal("70.00")))
                .build();
        existing.setId(UUID.randomUUID());
        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), UUID.randomUUID(), SystemRole.HR_OFFICER,
                "hr@example.com"));
        when(repository.findByDepartmentIdAndEffectiveDate(departmentId, effectiveDate))
                .thenReturn(List.of(existing));

        KpiPolicyService service = new KpiPolicyService(
                repository, departmentRepository, personRepository, mapper, currentUserService);

        assertThatThrownBy(() -> service.create(new CreateKpiPolicyRequest(
                departmentId,
                effectiveDate,
                PerformanceRating.EXCELLENT.label(),
                new BigDecimal("60.00"),
                new BigDecimal("90.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                UUID.randomUUID())))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                PerformanceErrorCode.KPI_TIER_OVERLAP));

        verify(repository, never()).save(any());
    }
}
