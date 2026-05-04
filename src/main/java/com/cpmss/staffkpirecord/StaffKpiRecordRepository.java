package com.cpmss.staffkpirecord;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link StaffKpiRecord} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface StaffKpiRecordRepository
        extends JpaRepository<StaffKpiRecord, StaffKpiRecordId> {
}
