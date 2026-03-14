package com.cpmss.workorder;

import com.cpmss.company.Company;
import com.cpmss.company.CompanyRepository;
import com.cpmss.facility.FacilityRepository;
import com.cpmss.person.PersonRepository;
import com.cpmss.unit.UnitRepository;
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
    private final PersonRepository personRepository;
    private final UnitRepository unitRepository;
    private final FacilityRepository facilityRepository;

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

    /**
     * Create a work order with full business rule validation.
     */
    @Override
    @Transactional
    public WorkOrder create(WorkOrder workOrder) {
        // --- Business Rules ---

        // 1. Must have a requester
        if (workOrder.getRequestedBy() == null) {
            throw new IllegalArgumentException("A work order must have a requester.");
        }

        // 2. Must have at least one target (unit OR facility)
        if (workOrder.getTargetUnit() == null && workOrder.getTargetFacility() == null) {
            throw new IllegalArgumentException("A work order must target a unit or facility.");
        }

        // 3. Scheduled date cannot be in the past
        if (workOrder.getDateScheduled() != null && workOrder.getDateScheduled().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date cannot be in the past.");
        }

        // 4. Cost must be non-negative
        if (workOrder.getCostAmount() != null && workOrder.getCostAmount().signum() < 0) {
            throw new IllegalArgumentException("Cost cannot be negative.");
        }

        workOrder.setJobStatus("Pending");
        return workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional
    public WorkOrder assign(UUID id, UUID companyId) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work order not found: " + id));

        // Business rule: can only assign Pending work orders
        if (!"Pending".equals(wo.getJobStatus())) {
            throw new IllegalStateException("Can only assign work orders in Pending status, current: " + wo.getJobStatus());
        }

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

        // Business rule: can only complete Assigned or Pending work orders
        if ("Completed".equals(wo.getJobStatus())) {
            throw new IllegalStateException("Work order is already completed.");
        }

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
