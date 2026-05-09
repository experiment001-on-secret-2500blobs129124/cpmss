package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;

import java.time.LocalDate;

/**
 * Calendar-date range with a required start date and optional end date.
 *
 * <p>Use this value object for domain periods where an open-ended range is
 * valid, such as active contracts, residency periods, position histories, or
 * permit validity windows. It validates date ordering without performing any
 * repository-backed workflow checks.
 *
 * @param startDate the first date included in the range
 * @param endDate the optional last date included in the range
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {

    /**
     * Creates a date range.
     *
     * @throws ApiException if the start date is missing or the end date
     *                      is before the start date
     */
    public DateRange {
        if (startDate == null) {
            throw new ApiException(CommonErrorCode.DATE_RANGE_INVALID, "Start date is required");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new ApiException(CommonErrorCode.DATE_RANGE_INVALID, "End date cannot be before start date");
        }
    }

    /**
     * Checks whether a date falls inside this range.
     *
     * @param date the date to test
     * @return true when the date is on or after the start and, when present,
     *         on or before the end
     * @throws ApiException if the date is missing
     */
    public boolean contains(LocalDate date) {
        if (date == null) {
            throw new ApiException(CommonErrorCode.DATE_RANGE_INVALID, "Date is required");
        }
        return !date.isBefore(startDate) && (endDate == null || !date.isAfter(endDate));
    }
}
