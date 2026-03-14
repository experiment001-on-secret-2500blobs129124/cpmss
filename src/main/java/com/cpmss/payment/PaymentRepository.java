package com.cpmss.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByDirection(String direction);

    List<Payment> findByInstallmentId(UUID installmentId);

    List<Payment> findByReconciliationStatus(String status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.direction = :direction")
    BigDecimal sumByDirection(String direction);
}
