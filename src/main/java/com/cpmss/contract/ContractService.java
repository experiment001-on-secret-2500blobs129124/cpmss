package com.cpmss.contract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractService {
    List<Contract> findAll();
    Optional<Contract> findById(UUID id);
    List<Contract> findByStatus(String status);
    Contract create(Contract contract);
    Contract activate(UUID id);
    Contract terminate(UUID id);
    long count();
    long countByStatus(String status);
}
