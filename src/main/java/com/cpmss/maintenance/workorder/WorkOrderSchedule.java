package com.cpmss.maintenance.workorder;

import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cpmss.platform.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Optional schedule/completion date pair for a work order.
 *
 * <p>This value mirrors the current Flyway date rule: when both dates are
 * present, completion cannot be before the scheduled date. Workflow rules
 * such as requiring scheduling before completion remain outside this value.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkOrderSchedule {

    /** Scheduled date for the work. */
    @Column(name = "date_scheduled")
    private LocalDate dateScheduled;

    /** Date the work was completed. */
    @Column(name = "date_completed")
    private LocalDate dateCompleted;

    /**
     * Creates a work-order schedule.
     *
     * @param dateScheduled the optional scheduled date
     * @param dateCompleted the optional completed date
     * @throws ApiException if completion is before scheduling
     */
    @JsonCreator
    public WorkOrderSchedule(@JsonProperty("dateScheduled") LocalDate dateScheduled,
                             @JsonProperty("dateCompleted") LocalDate dateCompleted) {
        if (dateScheduled != null && dateCompleted != null && dateCompleted.isBefore(dateScheduled)) {
            throw new ApiException(MaintenanceErrorCode.WORK_ORDER_SCHEDULE_INVALID);
        }
        this.dateScheduled = dateScheduled;
        this.dateCompleted = dateCompleted;
    }
}
