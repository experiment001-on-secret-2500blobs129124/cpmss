package com.cpmss.unitstatushistory;

import com.cpmss.common.BaseAuditEntity;
import com.cpmss.unit.Unit;
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

/**
 * SCD Type 2 entity tracking occupancy status of a unit over time.
 *
 * <p>Composite PK: ({@code unit_id}, {@code effective_date}).
 * Valid statuses: Vacant, Occupied, Under Maintenance, Reserved.
 * {@code ORDER BY effective_date DESC LIMIT 1} = current status.
 */
@Entity
@Table(name = "Unit_Status_History")
@IdClass(UnitStatusHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitStatusHistory extends BaseAuditEntity {

    /** The unit whose status is tracked (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    /** The date this status became effective (part of composite PK). */
    @Id
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /** The occupancy status at this effective date. */
    @Column(name = "unit_status", nullable = false, length = 50)
    private String unitStatus;
}
