package com.cpmss.installment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {

    List<Installment> findByContractIdOrderByDueDateAsc(UUID contractId);

    List<Installment> findByStatus(String status);

    List<Installment> findByDueDateBeforeAndStatus(LocalDate date, String status);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(i.amountExpected), 0) FROM Installment i WHERE i.status = :status")
    BigDecimal sumAmountByStatus(String status);
}
