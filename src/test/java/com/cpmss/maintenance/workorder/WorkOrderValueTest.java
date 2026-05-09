package com.cpmss.maintenance.workorder;

import com.cpmss.platform.exception.BusinessException;
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
                .isInstanceOf(BusinessException.class)
                .hasMessage("Work order completion date cannot be before scheduled date");
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
                .isInstanceOf(BusinessException.class)
                .hasMessage("Work order status must be one of: Pending, Assigned, In Progress, Completed, Paid, Cancelled");
        assertThatThrownBy(() -> WorkOrderPriority.fromLabel("Critical"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Work order priority must be one of: Low, Normal, High, Emergency");
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
