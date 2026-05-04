package com.cpmss.staffpositionhistory;

import com.cpmss.common.BaseAuditEntity;
import com.cpmss.person.Person;
import com.cpmss.staffposition.StaffPosition;
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
 * SCD Type 2 entity tracking which position a staff member holds over time.
 *
 * <p>Composite PK: ({@code person_id}, {@code position_id}, {@code effective_date}).
 * A row with {@code end_date IS NULL} represents the currently active assignment.
 *
 * @see StaffPosition
 */
@Entity
@Table(name = "Staff_Position_History")
@IdClass(StaffPositionHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffPositionHistory extends BaseAuditEntity {

    /** The staff member holding the position (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    /** The position being held (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private StaffPosition position;

    /** The date this assignment became effective (part of composite PK). */
    @Id
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /** The date this assignment ended ({@code null} = still active). */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** The manager who authorized this position change ({@code null} for initial hire). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_by_id")
    private Person authorizedBy;
}
