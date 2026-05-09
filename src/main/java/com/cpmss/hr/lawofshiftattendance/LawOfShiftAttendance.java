package com.cpmss.hr.lawofshiftattendance;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.platform.common.value.HoursAmount;
import com.cpmss.platform.common.value.HoursAmountConverter;
import com.cpmss.platform.common.value.LocalTimeWindow;
import com.cpmss.workforce.common.ShiftTimeWindow;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * SCD Type 2 entity defining the rule set for a shift type at a point in time.
 *
 * <p>Composite PK: ({@code shift_id}, {@code effective_date}).
 * {@code expected_hours} anchors the daily pay calculation —
 * backend uses {@code base_daily_rate × (diff_hour / expected_hours)}.
 *
 * @see ShiftAttendanceType
 */
@Entity
@Table(name = "Law_of_Shift_Attendance")
@IdClass(LawOfShiftAttendanceId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LawOfShiftAttendance extends BaseAuditEntity {

    /** The shift type this rule applies to (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftAttendanceType shift;

    /** The date this rule set became effective (part of composite PK). */
    @Id
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    /** Required same-day shift time window. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startTime",
                    column = @Column(name = "start_time", nullable = false)),
            @AttributeOverride(name = "endTime",
                    column = @Column(name = "end_time", nullable = false))
    })
    @Setter(lombok.AccessLevel.NONE)
    private ShiftTimeWindow shiftTimeWindow;

    /** Expected working hours per shift — basis for daily pay fraction. */
    @Convert(converter = HoursAmountConverter.class)
    @Column(name = "expected_hours", nullable = false, precision = 4, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private HoursAmount expectedHours;

    /** Bonus money per hour worked above expected hours (overtime). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "one_hour_extra_bonus", precision = 8, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "one_hour_extra_bonus_currency", length = 10))
    })
    private Money oneHourExtraBonus;

    /** Deduction money per hour below expected hours (lateness/early leave). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "one_hour_diff_discount", precision = 8, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "one_hour_diff_discount_currency", length = 10))
    })
    private Money oneHourDiffDiscount;

    /** Optional label describing the shift period range. */
    @Column(name = "period_start_end", length = 50)
    private String periodStartEnd;

    /**
     * Returns the shift time window for domain logic.
     *
     * @return the same-day shift time window
     */
    public LocalTimeWindow getShiftWindow() {
        return new LocalTimeWindow(shiftTimeWindow.getStartTime(), shiftTimeWindow.getEndTime());
    }

    /**
     * Returns the shift start time for compatibility with existing callers.
     *
     * @return the shift start time
     */
    public LocalTime getStartTime() {
        return shiftTimeWindow != null ? shiftTimeWindow.getStartTime() : null;
    }

    /**
     * Returns the shift end time for compatibility with existing callers.
     *
     * @return the shift end time
     */
    public LocalTime getEndTime() {
        return shiftTimeWindow != null ? shiftTimeWindow.getEndTime() : null;
    }

    /**
     * Assigns the required shift time window.
     *
     * @param shiftTimeWindow the shift time window
     */
    public void setShiftTimeWindow(ShiftTimeWindow shiftTimeWindow) {
        this.shiftTimeWindow = shiftTimeWindow;
    }

    /**
     * Returns the expected hours amount for DTO compatibility.
     *
     * @return the expected hours, or {@code null} when unset
     */
    public BigDecimal getExpectedHours() {
        return expectedHours != null ? expectedHours.hours() : null;
    }

    /**
     * Returns the typed expected hours amount for domain logic.
     *
     * @return the typed expected hours, or {@code null} when unset
     */
    public HoursAmount getExpectedHoursValue() {
        return expectedHours;
    }

    /**
     * Assigns the required expected hours amount.
     *
     * @param expectedHours the expected shift hours
     */
    public void setExpectedHours(BigDecimal expectedHours) {
        this.expectedHours = HoursAmount.positive(expectedHours);
    }
}
