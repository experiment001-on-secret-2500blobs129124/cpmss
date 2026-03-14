package com.cpmss.workorder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderService {
    List<WorkOrder> findAll();
    Optional<WorkOrder> findById(UUID id);
    List<WorkOrder> findByStatus(String status);
    WorkOrder create(WorkOrder workOrder);
    WorkOrder assign(UUID id, UUID companyId);
    WorkOrder complete(UUID id);
    long count();
    long countByStatus(String status);
}
