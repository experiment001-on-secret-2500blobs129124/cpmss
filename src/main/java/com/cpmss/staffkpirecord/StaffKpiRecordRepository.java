package com.cpmss.staffkpirecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link StaffKpiRecord} entities.
 */
public interface StaffKpiRecordRepository
        extends JpaRepository<StaffKpiRecord, StaffKpiRecordId> {

    /**
     * Find all KPI records for a staff member in a department within a date range.
     */
    List<StaffKpiRecord> findByStaffIdAndDepartmentIdAndRecordDateBetween(
            UUID staffId, UUID departmentId, LocalDate from, LocalDate to);

    /**
     * Find all KPI records for a staff member within a date range (any department).
     */
    List<StaffKpiRecord> findByStaffIdAndRecordDateBetween(
            UUID staffId, LocalDate from, LocalDate to);

    /**
     * Find all KPI records for a department within a date range.
     */
    List<StaffKpiRecord> findByDepartmentIdAndRecordDateBetween(
            UUID departmentId, LocalDate from, LocalDate to);
}
