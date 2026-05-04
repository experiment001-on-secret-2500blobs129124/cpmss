package com.cpmss.entersat;

import com.cpmss.accesspermit.AccessPermit;
import com.cpmss.common.BaseEntity;
import com.cpmss.gate.Gate;
import com.cpmss.person.Person;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Junction entity recording every gate access event.
 *
 * <p>Either a permit scan or an anonymous plate-only log.
 * Exactly one of {@code permit_id} or {@code manual_plate_entry}
 * must be provided — enforced in {@link EntersAtRules} and V2.
 */
@Entity
@Table(name = "Enters_At")
@AttributeOverride(name = "id", column = @Column(name = "entry_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntersAt extends BaseEntity {

    /** The gate where the event occurred. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gate_id", nullable = false)
    private Gate gate;

    /** Access permit used for this entry ({@code null} for anonymous). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permit_id")
    private AccessPermit permit;

    /** Manually entered plate number for unregistered vehicles. */
    @Column(name = "manual_plate_entry", length = 20)
    private String manualPlateEntry;

    /** Exact timestamp of the gate event. */
    @Column(name = "entered_at", nullable = false)
    private Instant enteredAt;

    /** Direction of travel (IN or OUT). */
    @Column(name = "direction", nullable = false, length = 10)
    private String direction;

    /** Purpose of the visit. */
    @Column(name = "purpose", length = 100)
    private String purpose;

    /** Guard who processed this event. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id")
    private Person processedBy;

    /** Resident/staff the visitor is coming to see. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id")
    private Person requestedBy;
}
