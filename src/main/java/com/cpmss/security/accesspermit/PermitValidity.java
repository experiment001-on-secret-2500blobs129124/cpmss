package com.cpmss.security.accesspermit;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Issue and expiry dates for an access permit.
 *
 * <p>The expiry date is optional. When present, it must be on or after the
 * issue date, matching the Flyway V2 {@code chk_permit_dates} constraint.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermitValidity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Date the permit was issued. */
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    /** Optional date the permit expires. */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Creates a permit validity period.
     *
     * @param issueDate the required issue date
     * @param expiryDate the optional expiry date
     * @throws ApiException if the issue date is missing or the expiry
     *                           date is before the issue date
     */
    public PermitValidity(LocalDate issueDate, LocalDate expiryDate) {
        if (issueDate == null) {
            throw new ApiException(SecurityErrorCode.PERMIT_ISSUE_DATE_REQUIRED);
        }
        if (expiryDate != null && expiryDate.isBefore(issueDate)) {
            throw new ApiException(SecurityErrorCode.PERMIT_DATE_RANGE_INVALID);
        }
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    /**
     * Checks whether the permit is date-valid on a given date.
     *
     * @param date the date to check
     * @return true when the date is on or after issue date and not after
     *         expiry date when one exists
     * @throws ApiException if the date is missing
     */
    public boolean contains(LocalDate date) {
        if (date == null) {
            throw new ApiException(SecurityErrorCode.PERMIT_CHECK_DATE_REQUIRED);
        }
        return !date.isBefore(issueDate) && (expiryDate == null || !date.isAfter(expiryDate));
    }
}
