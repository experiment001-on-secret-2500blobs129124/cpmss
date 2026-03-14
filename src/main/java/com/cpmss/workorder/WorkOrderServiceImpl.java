package com.cpmss.workorder;

import com.cpmss.company.Company;
import com.cpmss.company.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CompanyRepository companyRepository;

    @Override
    public List<WorkOrder> findAll() {
        return workOrderRepository.findAll();
    }

    @Override
    public Optional<WorkOrder> findById(UUID id) {
        return workOrderRepository.findById(id);
    }

    @Override
    public List<WorkOrder> findByStatus(String status) {
        return workOrderRepository.findByJobStatus(status);
    }

    @Override
    @Transactional
    public WorkOrder create(WorkOrder workOrder) {
        workOrder.setJobStatus("Pending");
        return workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional
    public WorkOrder assign(UUID id, UUID companyId) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work order not found: " + id));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found: " + companyId));

        wo.setAssignedCompany(company);
        wo.setDateAssigned(LocalDate.now());
        wo.setJobStatus("Assigned");
        return workOrderRepository.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder complete(UUID id) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work order not found: " + id));
        wo.setJobStatus("Completed");
        wo.setDateCompleted(LocalDate.now());
        return workOrderRepository.save(wo);
    }

    @Override
    public long count() {
        return workOrderRepository.count();
    }

    @Override
    public long countByStatus(String status) {
        return workOrderRepository.countByJobStatus(status);
    }
}
