package com.cpmss.performance.staffkpirecord;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.PercentageRate;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.kpipolicy.KpiPolicy;
import com.cpmss.performance.kpipolicy.KpiPolicyRepository;
import com.cpmss.performance.staffkpimonthlysummary.StaffKpiMonthlySummary;
import com.cpmss.performance.staffkpimonthlysummary.StaffKpiMonthlySummaryRepository;
import com.cpmss.performance.staffkpirecord.dto.CreateStaffKpiRecordRequest;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies KPI record and monthly-close policy-tier workflow behavior.
 */
@ExtendWith(MockitoExtension.class)
class KpiServicePolicyWorkflowTest {

    @Mock
    private StaffKpiRecordRepository kpiRecordRepository;

    @Mock
    private StaffKpiMonthlySummaryRepository kpiSummaryRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private KpiPolicyRepository kpiPolicyRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private DepartmentScopeService departmentScopeService;

    @Test
    void rejectsDailyRecordUsingInactiveOlderPolicyVersion() {
        UUID staffId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID policyId = UUID.randomUUID();
        UUID recorderId = UUID.randomUUID();
        LocalDate recordDate = LocalDate.of(2026, 2, 15);
        Department department = department(departmentId);
        KpiPolicy olderPolicy = policy(
                policyId,
                department,
                LocalDate.of(2026, 1, 1),
                "80.00",
                "100.00",
                PerformanceRating.EXCELLENT);
        KpiPolicy activePolicy = policy(
                UUID.randomUUID(),
                department,
                LocalDate.of(2026, 2, 1),
                "80.00",
                "100.00",
                PerformanceRating.EXCELLENT);

        when(currentUserService.currentUser()).thenReturn(hrUser(recorderId));
        when(kpiRecordRepository.existsByStaffIdAndDepartmentIdAndRecordDate(
                staffId, departmentId, recordDate)).thenReturn(false);
        when(personRepository.findById(staffId)).thenReturn(Optional.of(person(staffId)));
        when(personRepository.findById(recorderId)).thenReturn(Optional.of(person(recorderId)));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(kpiPolicyRepository.findById(policyId)).thenReturn(Optional.of(olderPolicy));
        when(kpiPolicyRepository.findByDepartmentIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                departmentId, recordDate)).thenReturn(List.of(activePolicy, olderPolicy));

        assertThatThrownBy(() -> service().recordDailyKpi(new CreateStaffKpiRecordRequest(
                staffId,
                departmentId,
                recordDate,
                new BigDecimal("90.00"),
                policyId,
                recorderId,
                "monthly observation")))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                PerformanceErrorCode.KPI_POLICY_NOT_ACTIVE));

        verify(kpiRecordRepository, never()).save(any());
    }

    @Test
    void monthlyCloseAppliesActiveTierThatMatchesAverageScore() {
        UUID staffId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID closerId = UUID.randomUUID();
        Department department = department(departmentId);
        Person staff = person(staffId);
        Person closer = person(closerId);
        LocalDate periodDate = LocalDate.of(2026, 5, 31);
        KpiPolicy averageTier = policy(
                UUID.randomUUID(),
                department,
                LocalDate.of(2026, 1, 1),
                "0.00",
                "79.99",
                PerformanceRating.AVERAGE);
        KpiPolicy excellentTier = policy(
                UUID.randomUUID(),
                department,
                LocalDate.of(2026, 1, 1),
                "80.00",
                "100.00",
                PerformanceRating.EXCELLENT);
        StaffKpiRecord record = record(staff, department, periodDate,
                "85.00", excellentTier, closer);

        when(currentUserService.currentUser()).thenReturn(hrUser(closerId));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(personRepository.findById(closerId)).thenReturn(Optional.of(closer));
        when(kpiRecordRepository.findByDepartmentIdAndRecordDateBetween(
                departmentId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31)))
                .thenReturn(List.of(record));
        when(kpiSummaryRepository.existsByStaffIdAndDepartmentIdAndYearAndMonth(
                staffId, departmentId, 2026, 5)).thenReturn(false);
        when(kpiPolicyRepository.findByDepartmentIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                departmentId, LocalDate.of(2026, 5, 31)))
                .thenReturn(List.of(averageTier, excellentTier));
        when(kpiSummaryRepository.save(any(StaffKpiMonthlySummary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service().closeMonthlyKpi(departmentId, 2026, 5, closerId);

        ArgumentCaptor<StaffKpiMonthlySummary> summaryCaptor =
                ArgumentCaptor.forClass(StaffKpiMonthlySummary.class);
        verify(kpiSummaryRepository).save(summaryCaptor.capture());
        StaffKpiMonthlySummary summary = summaryCaptor.getValue();
        assertThat(summary.getApplicableTier()).isEqualTo(PerformanceRating.EXCELLENT.label());
        assertThat(summary.getKpiPolicy().getId()).isEqualTo(excellentTier.getId());
    }

    private KpiService service() {
        return new KpiService(
                kpiRecordRepository,
                kpiSummaryRepository,
                personRepository,
                departmentRepository,
                kpiPolicyRepository,
                currentUserService,
                departmentScopeService);
    }

    private static CurrentUser hrUser(UUID personId) {
        return new CurrentUser(UUID.randomUUID(), personId, SystemRole.HR_OFFICER,
                "hr@example.com");
    }

    private static Department department(UUID id) {
        Department department = new Department();
        department.setId(id);
        return department;
    }

    private static Person person(UUID id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }

    private static KpiPolicy policy(UUID id,
                                    Department department,
                                    LocalDate effectiveDate,
                                    String min,
                                    String max,
                                    PerformanceRating tier) {
        KpiPolicy policy = KpiPolicy.builder()
                .department(department)
                .effectiveDate(effectiveDate)
                .tierLabel(tier)
                .minKpiScore(KpiScore.of(new BigDecimal(min)))
                .maxKpiScore(KpiScore.of(new BigDecimal(max)))
                .bonusRate(PercentageRate.ZERO)
                .deductionRate(PercentageRate.ZERO)
                .approvedBy(person(UUID.randomUUID()))
                .build();
        policy.setId(id);
        return policy;
    }

    private static StaffKpiRecord record(Person staff,
                                         Department department,
                                         LocalDate recordDate,
                                         String score,
                                         KpiPolicy policy,
                                         Person recordedBy) {
        StaffKpiRecord record = new StaffKpiRecord();
        record.setStaff(staff);
        record.setDepartment(department);
        record.setRecordDate(recordDate);
        record.setKpiScore(KpiScore.of(new BigDecimal(score)));
        record.setKpiPolicy(policy);
        record.setRecordedBy(recordedBy);
        return record;
    }
}
