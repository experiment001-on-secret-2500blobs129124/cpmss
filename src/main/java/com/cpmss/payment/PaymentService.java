package com.cpmss.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    List<Payment> findAll();
    Optional<Payment> findById(UUID id);
    List<Payment> findByInstallment(UUID installmentId);
    Payment recordPayment(Payment payment);
    BigDecimal totalInbound();
    BigDecimal totalOutbound();
}
