package com.cpmss.leasing.common;

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
 * Contract date period stored in {@code Contract.start_date/end_date}.
 *
 * <p>The Flyway constraint requires a present start date and, when present,
 * an end date strictly after the start date. This value object keeps that
 * schema rule in Java without adding workflow behavior such as renewal or
 * expiry handling.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractPeriod {

    /** Date the contract starts. */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Optional date the contract ends. */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Creates a contract period.
     *
     * @param startDate the required contract start date
     * @param endDate the optional contract end date
     * @throws ApiException if the start date is missing or the end date
     *                           is not after the start date
     */
    @JsonCreator
    public ContractPeriod(@JsonProperty("startDate") LocalDate startDate,
                          @JsonProperty("endDate") LocalDate endDate) {
        if (startDate == null) {
            throw new ApiException(LeasingErrorCode.CONTRACT_START_DATE_REQUIRED);
        }
        if (endDate != null && !endDate.isAfter(startDate)) {
            throw new ApiException(LeasingErrorCode.CONTRACT_END_DATE_INVALID);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
