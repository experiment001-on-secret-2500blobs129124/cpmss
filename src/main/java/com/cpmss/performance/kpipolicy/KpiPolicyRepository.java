package com.cpmss.performance.kpipolicy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link KpiPolicy} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support tier
 * overlap checks and active tier resolution by department/date.
 */
public interface KpiPolicyRepository extends JpaRepository<KpiPolicy, UUID> {

    /**
     * Finds tiers for a department with the same effective date.
     *
     * @param departmentId the owning department UUID
     * @param effectiveDate the policy effective date
     * @return KPI tiers for that policy version
     */
    List<KpiPolicy> findByDepartmentIdAndEffectiveDate(UUID departmentId, LocalDate effectiveDate);

    /**
     * Finds active-or-older tiers for a department, newest policy version first.
     *
     * @param departmentId the owning department UUID
     * @param effectiveDate latest acceptable effective date
     * @return matching policy tiers ordered newest first
     */
    List<KpiPolicy> findByDepartmentIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            UUID departmentId, LocalDate effectiveDate);
}
