package com.cpmss.maintenance.workorder;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.maintenance.workorderassignedto.WorkOrderAssignedTo;
import com.cpmss.maintenance.workorderassignedto.WorkOrderAssignedToRepository;
import com.cpmss.maintenance.workorderassignedto.dto.AssignWorkOrderCompanyRequest;
import com.cpmss.maintenance.workorderassignedto.dto.WorkOrderAssignmentResponse;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.facility.FacilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies the parent-managed work-order vendor assignment workflow.
 */
@ExtendWith(MockitoExtension.class)
class WorkOrderAssignmentServiceTest {

    @Mock
    private WorkOrderRepository repository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private WorkOrderAssignedToRepository assignmentRepository;

    @Mock
    private WorkOrderMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void assignsCompanyAndMirrorsCurrentVendorOnWorkOrder() {
        UUID workOrderId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        WorkOrder workOrder = workOrder(workOrderId);
        Company company = company(companyId);
        LocalDate assignedOn = LocalDate.of(2026, 5, 10);
        when(currentUserService.currentUser()).thenReturn(facilityOfficer());
        when(repository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(assignmentRepository.existsByWorkOrderIdAndCompanyId(workOrderId, companyId))
                .thenReturn(false);
        when(assignmentRepository.save(any(WorkOrderAssignedTo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrderAssignmentResponse response = service().assignCompany(
                workOrderId, new AssignWorkOrderCompanyRequest(companyId, assignedOn));

        ArgumentCaptor<WorkOrderAssignedTo> assignmentCaptor =
                ArgumentCaptor.forClass(WorkOrderAssignedTo.class);
        verify(assignmentRepository).save(assignmentCaptor.capture());
        verify(repository).save(workOrder);
        assertThat(workOrder.getCompany()).isSameAs(company);
        assertThat(workOrder.getJobStatus()).isEqualTo(WorkOrderStatus.ASSIGNED);
        assertThat(assignmentCaptor.getValue().getWorkOrder()).isSameAs(workOrder);
        assertThat(assignmentCaptor.getValue().getCompany()).isSameAs(company);
        assertThat(response.companyId()).isEqualTo(companyId);
        assertThat(response.dateAssigned()).isEqualTo(assignedOn);
    }

    @Test
    void rejectsDuplicateWorkOrderAssignment() {
        UUID workOrderId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(facilityOfficer());
        when(repository.findById(workOrderId)).thenReturn(Optional.of(workOrder(workOrderId)));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company(companyId)));
        when(assignmentRepository.existsByWorkOrderIdAndCompanyId(workOrderId, companyId))
                .thenReturn(true);

        assertThatThrownBy(() -> service().assignCompany(
                workOrderId,
                new AssignWorkOrderCompanyRequest(companyId, LocalDate.of(2026, 5, 10))))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                MaintenanceErrorCode.WORK_ORDER_ASSIGNMENT_DUPLICATE));
        verify(assignmentRepository, never()).save(any());
    }

    private WorkOrderService service() {
        return new WorkOrderService(repository, personRepository, facilityRepository,
                companyRepository, assignmentRepository, mapper, currentUserService);
    }

    private static CurrentUser facilityOfficer() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.FACILITY_OFFICER, "facility@example.com");
    }

    private static WorkOrder workOrder(UUID id) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(id);
        workOrder.setJobStatus(WorkOrderStatus.PENDING);
        return workOrder;
    }

    private static Company company(UUID id) {
        Company company = new Company();
        company.setId(id);
        return company;
    }
}
