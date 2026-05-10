package com.cpmss.hr.lawofshiftattendance.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.workforce.common.ShiftTimeWindow;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for adding a dated attendance law to a shift type.
 *
 * @param effectiveDate        the date the law becomes effective
 * @param shiftTimeWindow      the expected shift time window
 * @param expectedHours        the expected working hours
 * @param oneHourExtraBonus    overtime bonus per hour
 * @param oneHourDiffDiscount  shortfall deduction per hour
 * @param periodStartEnd       optional shift period label
 */
public record CreateShiftAttendanceLawRequest(
        @NotNull LocalDate effectiveDate,
        @NotNull @Valid ShiftTimeWindow shiftTimeWindow,
        @NotNull BigDecimal expectedHours,
        @Valid Money oneHourExtraBonus,
        @Valid Money oneHourDiffDiscount,
        String periodStartEnd
) {}
