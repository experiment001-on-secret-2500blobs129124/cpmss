package com.cpmss.leasing.common;

import com.cpmss.platform.exception.BusinessException;

import java.time.LocalDate;

/**
 * Date period for a resident living under a contract.
 *
 * <p>The table keeps {@code move_in_date} in the composite primary key, so
 * this value object is used by services and entity setters rather than as an
 * embedded identifier. It mirrors the Flyway V2 residency date constraint.
 *
 * @param moveInDate the required move-in date
 * @param moveOutDate the optional move-out date
 */
public record ResidencyPeriod(LocalDate moveInDate, LocalDate moveOutDate) {

    /**
     * Creates a residency period.
     *
     * @throws BusinessException if the move-in date is missing or the
     *                           move-out date is not after it
     */
    public ResidencyPeriod {
        if (moveInDate == null) {
            throw new BusinessException("Move-in date is required");
        }
        if (moveOutDate != null && !moveOutDate.isAfter(moveInDate)) {
            throw new BusinessException("Move-out date must be after move-in date");
        }
    }
}
