package com.cpmss.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findByContractStatus(String status);

    List<Contract> findByContractType(String type);

    List<Contract> findByEndDateBefore(LocalDate date);

    long countByContractStatus(String status);
}
