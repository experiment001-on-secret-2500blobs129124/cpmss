package com.cpmss.maintenance.workorder;

import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkOrderValueTest {

    @Test
    void acceptsCompletionOnOrAfterScheduledDate() {
        WorkOrderSchedule schedule = new WorkOrderSchedule(
                LocalDate.of(2026, 5, 8),
                LocalDate.of(2026, 5, 9));

        assertThat(schedule.getDateScheduled()).isEqualTo(LocalDate.of(2026, 5, 8));
        assertThat(schedule.getDateCompleted()).isEqualTo(LocalDate.of(2026, 5, 9));
    }

    @Test
    void rejectsCompletionBeforeScheduledDate() {
        assertThatThrownBy(() -> new WorkOrderSchedule(
                LocalDate.of(2026, 5, 8),
                LocalDate.of(2026, 5, 7)))
            .isInstanceOfSatisfying(ApiException.class,
                ex -> assertThat(ex.getErrorCode())
                    .isEqualTo(MaintenanceErrorCode.WORK_ORDER_SCHEDULE_INVALID));
    }

    @Test
    void parsesWorkOrderVocabularyLabels() {
        assertThat(WorkOrderStatus.fromLabel("Pending")).isEqualTo(WorkOrderStatus.PENDING);
        assertThat(WorkOrderStatus.fromLabel("In Progress")).isEqualTo(WorkOrderStatus.IN_PROGRESS);
        assertThat(WorkOrderPriority.fromLabel("Emergency")).isEqualTo(WorkOrderPriority.EMERGENCY);
        assertThat(ServiceCategory.fromLabel("HVAC")).isEqualTo(ServiceCategory.HVAC);
    }

    @Test
    void rejectsUnknownWorkOrderVocabularyLabels() {
        assertThatThrownBy(() -> WorkOrderStatus.fromLabel("Open"))
            .isInstanceOfSatisfying(ApiException.class,
                ex -> assertThat(ex.getErrorCode())
                    .isEqualTo(MaintenanceErrorCode.WORK_ORDER_STATUS_INVALID));
        assertThatThrownBy(() -> WorkOrderPriority.fromLabel("Critical"))
            .isInstanceOfSatisfying(ApiException.class,
                ex -> assertThat(ex.getErrorCode())
                    .isEqualTo(MaintenanceErrorCode.WORK_ORDER_PRIORITY_INVALID));
    }

    @Test
    void enforcesWorkOrderStatusTransitions() {
        assertThat(WorkOrderStatus.PENDING.canTransitionTo(WorkOrderStatus.ASSIGNED)).isTrue();
        assertThat(WorkOrderStatus.ASSIGNED.canTransitionTo(WorkOrderStatus.IN_PROGRESS)).isTrue();
        assertThat(WorkOrderStatus.IN_PROGRESS.canTransitionTo(WorkOrderStatus.COMPLETED)).isTrue();
        assertThat(WorkOrderStatus.COMPLETED.canTransitionTo(WorkOrderStatus.PAID)).isTrue();
        assertThat(WorkOrderStatus.PAID.canTransitionTo(WorkOrderStatus.COMPLETED)).isFalse();
        assertThat(WorkOrderStatus.CANCELLED.canTransitionTo(WorkOrderStatus.PENDING)).isFalse();
    }

    @Test
    void convertersPreserveDatabaseLabels() {
        assertThat(new WorkOrderStatusConverter().convertToDatabaseColumn(WorkOrderStatus.ASSIGNED))
                .isEqualTo("Assigned");
        assertThat(new WorkOrderPriorityConverter().convertToDatabaseColumn(WorkOrderPriority.NORMAL))
                .isEqualTo("Normal");
        assertThat(new ServiceCategoryConverter().convertToDatabaseColumn(ServiceCategory.LANDSCAPING))
                .isEqualTo("Landscaping");
    }
}
