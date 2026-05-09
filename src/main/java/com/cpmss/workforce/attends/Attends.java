package com.cpmss.workforce.attends;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.people.person.Person;
import com.cpmss.workforce.common.AttendanceTimeWindow;
import com.cpmss.workforce.common.HourDelta;
import com.cpmss.workforce.common.HourDeltaConverter;
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

import java.time.LocalDate;

/**
 * Junction entity recording daily attendance for a staff member on a shift.
 *
 * <p>Composite PK: ({@code staff_id}, {@code shift_id}, {@code date}).
 * Daily salary fields are computed snapshots stored for auditability —
 * once payroll is run, these values are frozen.
 */
@Entity
@Table(name = "Attends")
@IdClass(AttendsId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attends extends BaseAuditEntity {

    /** The staff member (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Person staff;

    /** The shift type (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private ShiftAttendanceType shift;

    /** The attendance date (part of composite PK). */
    @Id
    @Column(name = "date")
    private LocalDate date;

    /** Whether the staff member was absent. */
    @Column(name = "is_absent", nullable = false)
    private Boolean isAbsent;

    /** Optional actual attendance window ({@code null} if absent). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "checkInTime",
                    column = @Column(name = "check_in_time")),
            @AttributeOverride(name = "checkOutTime",
                    column = @Column(name = "check_out_time"))
    })
    private AttendanceTimeWindow attendanceWindow;

    /** Period out-in description. */
    @Column(name = "period_out_in", length = 50)
    private String periodOutIn;

    /** Hours difference from expected (positive = overtime, negative = short). */
    @Convert(converter = HourDeltaConverter.class)
    @Column(name = "diff_hour", precision = 5, scale = 2)
    private HourDelta diffHour;

    /** Computed daily bonus money (frozen at payroll time). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "daily_bonus", precision = 10, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "daily_bonus_currency", length = 10))
    })
    private Money dailyBonus;

    /** Computed daily deduction money (frozen at payroll time). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "daily_deduction", precision = 10, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "daily_deduction_currency", length = 10))
    })
    private Money dailyDeduction;

    /** Computed daily salary money (frozen at payroll time). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "daily_salary", precision = 10, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "daily_salary_currency", length = 10))
    })
    private Money dailySalary;

    /** Computed daily net salary money (frozen at payroll time). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "daily_net_salary", precision = 10, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "daily_net_salary_currency", length = 10))
    })
    private Money dailyNetSalary;

}
