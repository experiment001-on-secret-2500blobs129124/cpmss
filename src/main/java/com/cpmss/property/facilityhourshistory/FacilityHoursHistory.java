package com.cpmss.property.facilityhourshistory;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.property.facility.Facility;
import jakarta.persistence.Column;
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
import java.time.LocalTime;

/**
 * SCD Type 2 entity tracking opening hours of a facility over time.
 *
 * <p>Composite PK: ({@code facility_id}, {@code effective_date}).
 * {@code ORDER BY effective_date DESC LIMIT 1} = current hours.
 */
@Entity
@Table(name = "Facility_Hours_History")
@IdClass(FacilityHoursHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityHoursHistory extends BaseAuditEntity {

    /** The facility whose hours are tracked (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    /** The date this hours schedule became effective (part of composite PK). */
    @Id
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /** Opening time of the facility. */
    @Column(name = "opening_time")
    private LocalTime openingTime;

    /** Closing time of the facility. */
    @Column(name = "closing_time")
    private LocalTime closingTime;

    /** Human-readable operating hours description (e.g. "6AM-10PM"). */
    @Column(name = "operating_hours", length = 50)
    private String operatingHours;
}
