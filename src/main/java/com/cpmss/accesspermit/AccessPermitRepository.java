package com.cpmss.accesspermit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccessPermitRepository extends JpaRepository<AccessPermit, UUID> {
    List<AccessPermit> findByHolderId(UUID holderId);
    List<AccessPermit> findByStatus(String status);
    long countByStatus(String status);
}
