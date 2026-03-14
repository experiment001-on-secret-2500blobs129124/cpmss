package com.cpmss.contract;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    @Override
    public Optional<Contract> findById(UUID id) {
        return contractRepository.findById(id);
    }

    @Override
    public List<Contract> findByStatus(String status) {
        return contractRepository.findByContractStatus(status);
    }

    @Override
    @Transactional
    public Contract create(Contract contract) {
        contract.setContractStatus("Draft");
        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Contract activate(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found: " + id));

        if (!"Draft".equals(contract.getContractStatus())) {
            throw new IllegalStateException(
                    "Only Draft contracts can be activated. Current status: " + contract.getContractStatus());
        }

        contract.setContractStatus("Active");
        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Contract terminate(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found: " + id));

        if (!"Active".equals(contract.getContractStatus())) {
            throw new IllegalStateException(
                    "Only Active contracts can be terminated. Current status: " + contract.getContractStatus());
        }

        contract.setContractStatus("Terminated");
        return contractRepository.save(contract);
    }

    @Override
    public long count() {
        return contractRepository.count();
    }

    @Override
    public long countByStatus(String status) {
        return contractRepository.countByContractStatus(status);
    }
}
