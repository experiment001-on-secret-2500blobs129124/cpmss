package com.cpmss.workforce.common;

import com.cpmss.finance.payrollpayment.PayrollPayment;
import com.cpmss.platform.common.value.YearMonthPeriod;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.attends.AttendsRules;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkforcePayrollValueTest {

    @Test
    void acceptsOrderedAttendanceWindows() {
        AttendanceTimeWindow window = new AttendanceTimeWindow(
                LocalTime.of(8, 0),
                LocalTime.of(16, 0));

        assertThat(window.getCheckInTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(window.getCheckOutTime()).isEqualTo(LocalTime.of(16, 0));
    }

    @Test
    void rejectsIncompleteAttendanceWindows() {
        assertThatThrownBy(() -> new AttendanceTimeWindow(LocalTime.of(8, 0), null))
                .isInstanceOf(ApiException.class)
                .hasMessage("Check-out time is required");
    }

    @Test
    void rejectsUnorderedAttendanceWindows() {
        assertThatThrownBy(() -> new AttendanceTimeWindow(
                LocalTime.of(8, 0),
                LocalTime.of(8, 0)))
                .isInstanceOf(ApiException.class)
                .hasMessage("Check-out time must be after check-in time");
    }

    @Test
    void acceptsOrderedShiftWindows() {
        ShiftTimeWindow window = new ShiftTimeWindow(
                LocalTime.of(7, 0),
                LocalTime.of(15, 0));

        assertThat(window.getStartTime()).isEqualTo(LocalTime.of(7, 0));
        assertThat(window.getEndTime()).isEqualTo(LocalTime.of(15, 0));
    }

    @Test
    void rejectsUnorderedShiftWindows() {
        assertThatThrownBy(() -> new ShiftTimeWindow(
                LocalTime.of(22, 0),
                LocalTime.of(6, 0)))
                .isInstanceOf(ApiException.class)
                .hasMessage("Shift end time must be after start time");
    }

    @Test
    void hourDeltaKeepsSignedAttendanceDifference() {
        HourDelta shortfall = new HourDelta(new BigDecimal("-1.50"));
        HourDelta overtime = new HourDelta(new BigDecimal("2.25"));

        assertThat(shortfall.isShortfall()).isTrue();
        assertThat(shortfall.isOvertime()).isFalse();
        assertThat(overtime.isOvertime()).isTrue();
    }

    @Test
    void hourDeltaConverterPreservesDatabaseValue() {
        HourDeltaConverter converter = new HourDeltaConverter();

        assertThat(converter.convertToDatabaseColumn(new HourDelta(new BigDecimal("-0.75"))))
                .isEqualByComparingTo("-0.75");
        assertThat(converter.convertToEntityAttribute(new BigDecimal("1.25")).hours())
                .isEqualByComparingTo("1.25");
    }

    @Test
    void attendanceRulesRequireWindowOnlyWhenPresent() {
        AttendsRules rules = new AttendsRules();

        assertThatCode(() -> rules.validateTimesWhenPresent(true, null))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.validateTimesWhenPresent(false, null))
                .isInstanceOf(ApiException.class)
                .hasMessage("Check-in and check-out times are required when staff is not absent");
    }

    @Test
    void payrollPeriodSettersCopyYearAndMonth() {
        YearMonthPeriod period = YearMonthPeriod.of(2026, 5);
        TaskMonthlySalary monthlySalary = new TaskMonthlySalary();
        PayrollPayment payrollPayment = new PayrollPayment();

        monthlySalary.setPayrollPeriod(period);
        payrollPayment.setPayrollPeriod(period);

        assertThat(monthlySalary.getYear()).isEqualTo(2026);
        assertThat(monthlySalary.getMonth()).isEqualTo(5);
        assertThat(payrollPayment.getPayrollPeriod()).isEqualTo(period);
    }

    @Test
    void payrollPeriodSettersRequirePeriod() {
        assertThatThrownBy(() -> new TaskMonthlySalary().setPayrollPeriod(null))
                .isInstanceOf(ApiException.class)
                .hasMessage("Payroll period is required");
        assertThatThrownBy(() -> new PayrollPayment().setPayrollPeriod(null))
                .isInstanceOf(ApiException.class)
                .hasMessage("Payroll period is required");
    }
}
