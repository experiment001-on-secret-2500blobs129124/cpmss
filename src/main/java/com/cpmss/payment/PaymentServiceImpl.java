package com.cpmss.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> findByInstallment(UUID installmentId) {
        return paymentRepository.findByInstallmentId(installmentId);
    }

    @Override
    @Transactional
    public Payment recordPayment(Payment payment) {
        // Payments are immutable — just save
        return paymentRepository.save(payment);
    }

    @Override
    public BigDecimal totalInbound() {
        return paymentRepository.sumByDirection("Inbound");
    }

    @Override
    public BigDecimal totalOutbound() {
        return paymentRepository.sumByDirection("Outbound");
    }
}
