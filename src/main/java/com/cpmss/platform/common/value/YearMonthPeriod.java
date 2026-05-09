package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Calendar month period used by payroll and KPI monthly snapshots.
 *
 * <p>The database stores the period as separate {@code year} and
 * {@code month} columns. This value object keeps validation and month boundary
 * calculations together while preserving those columns.
 *
 * @param year the four-digit calendar year
 * @param month the calendar month number from 1 to 12
 */
public record YearMonthPeriod(int year, int month) {

    /**
     * Creates a year-month period.
     *
     * @throws ApiException if the year or month is outside the supported
     *                      {@link YearMonth} range
     */
    public YearMonthPeriod {
        try {
            YearMonth.of(year, month);
        } catch (RuntimeException ex) {
            throw new ApiException(CommonErrorCode.YEAR_MONTH_PERIOD_INVALID);
        }
    }

    /**
     * Creates a period from primitive year and month values.
     *
     * @param year the calendar year
     * @param month the calendar month number from 1 to 12
     * @return the validated year-month period
     * @throws ApiException if the year or month is invalid
     */
    public static YearMonthPeriod of(int year, int month) {
        return new YearMonthPeriod(year, month);
    }

    /**
     * Returns the first date in this calendar month.
     *
     * @return the first day of the month
     */
    public LocalDate firstDay() {
        return yearMonth().atDay(1);
    }

    /**
     * Returns the last date in this calendar month.
     *
     * @return the last day of the month
     */
    public LocalDate lastDay() {
        return yearMonth().atEndOfMonth();
    }

    /**
     * Converts this value to Java's standard {@link YearMonth}.
     *
     * @return the equivalent Java year-month value
     */
    public YearMonth yearMonth() {
        return YearMonth.of(year, month);
    }
}
