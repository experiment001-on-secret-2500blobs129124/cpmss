package com.cpmss.hr.lawofshiftattendance.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.workforce.common.ShiftTimeWindow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a dated attendance law attached to a shift type.
 *
 * @param shiftId               the shift type UUID
 * @param effectiveDate         the date the law becomes effective
 * @param shiftTimeWindow       the expected shift time window
 * @param expectedHours         the expected working hours
 * @param oneHourExtraBonus     overtime bonus per hour
 * @param oneHourDiffDiscount   shortfall deduction per hour
 * @param periodStartEnd        optional shift period label
 */
public record ShiftAttendanceLawResponse(
        UUID shiftId,
        LocalDate effectiveDate,
        ShiftTimeWindow shiftTimeWindow,
        BigDecimal expectedHours,
        Money oneHourExtraBonus,
        Money oneHourDiffDiscount,
        String periodStartEnd
) {}
