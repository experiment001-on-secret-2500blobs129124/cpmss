package com.cpmss.entrylog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EntryLogRepository extends JpaRepository<EntryLog, UUID> {
    List<EntryLog> findByGateIdOrderByEntryTimestampDesc(UUID gateId);
    List<EntryLog> findByPermitIdOrderByEntryTimestampDesc(UUID permitId);
}
