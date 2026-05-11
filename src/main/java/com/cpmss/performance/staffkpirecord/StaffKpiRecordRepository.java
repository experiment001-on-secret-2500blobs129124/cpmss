package com.cpmss.performance.staffkpirecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link StaffKpiRecord} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for daily KPI score lookups by staff, department, and date range.
 */
public interface StaffKpiRecordRepository
        extends JpaRepository<StaffKpiRecord, StaffKpiRecordId> {

    /**
     * Finds all KPI records for a staff member in a department within a date range.
     *
     * @param staffId      the staff member's person UUID
     * @param departmentId the department UUID
     * @param from         start date (inclusive)
     * @param to           end date (inclusive)
     * @return KPI records matching the criteria
     */
    List<StaffKpiRecord> findByStaffIdAndDepartmentIdAndRecordDateBetween(
            UUID staffId, UUID departmentId, LocalDate from, LocalDate to);

    /**
     * Finds all KPI records for a staff member within a date range (any department).
     *
     * @param staffId the staff member's person UUID
     * @param from    start date (inclusive)
     * @param to      end date (inclusive)
     * @return KPI records for that staff member
     */
    List<StaffKpiRecord> findByStaffIdAndRecordDateBetween(
            UUID staffId, LocalDate from, LocalDate to);

    /**
     * Finds all KPI records for a department within a date range.
     *
     * @param departmentId the department UUID
     * @param from         start date (inclusive)
     * @param to           end date (inclusive)
     * @return KPI records for that department
     */
    List<StaffKpiRecord> findByDepartmentIdAndRecordDateBetween(
            UUID departmentId, LocalDate from, LocalDate to);

    /**
     * Checks whether a daily KPI record already exists for the staff/date scope.
     *
     * @param staffId the staff member's person UUID
     * @param departmentId the department UUID
     * @param recordDate the KPI record date
     * @return true when a record already exists
     */
    boolean existsByStaffIdAndDepartmentIdAndRecordDate(
            UUID staffId, UUID departmentId, LocalDate recordDate);
}
