package com.cpmss.security.entersat;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.people.person.Person;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.gate.Gate;
import com.cpmss.security.vehicle.LicensePlate;
import com.cpmss.security.vehicle.LicensePlateConverter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    @Convert(converter = LicensePlateConverter.class)
    @Column(name = "manual_plate_entry", length = 20)
    @Setter(lombok.AccessLevel.NONE)
    private LicensePlate manualPlateEntry;

    /** Exact timestamp of the gate event. */
    @Column(name = "entered_at", nullable = false)
    private Instant enteredAt;

    /** Direction of travel (In or Out). */
    @Convert(converter = GateDirectionConverter.class)
    @Column(name = "direction", nullable = false, length = 10)
    @Setter(lombok.AccessLevel.NONE)
    private GateDirection direction;

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

    /**
     * Returns the manual plate string for DTO compatibility.
     *
     * @return the normalized manual plate value, or {@code null} when absent
     */
    public String getManualPlateEntry() {
        return manualPlateEntry != null ? manualPlateEntry.value() : null;
    }

    /**
     * Returns the typed manual plate for domain logic.
     *
     * @return the typed manual plate, or {@code null} when absent
     */
    public LicensePlate getManualPlateEntryValue() {
        return manualPlateEntry;
    }

    /**
     * Returns the gate direction label for DTO compatibility.
     *
     * @return the database/API direction label, or {@code null} when unset
     */
    public String getDirection() {
        return direction != null ? direction.label() : null;
    }

    /**
     * Returns the typed gate direction for domain logic.
     *
     * @return the typed gate direction, or {@code null} when unset
     */
    public GateDirection getDirectionValue() {
        return direction;
    }

    /**
     * Assigns the optional manual plate value.
     *
     * @param manualPlateEntry the optional manual plate value
     */
    public void setManualPlateEntry(LicensePlate manualPlateEntry) {
        this.manualPlateEntry = manualPlateEntry;
    }

    /**
     * Assigns the typed gate direction.
     *
     * @param direction the typed gate direction
     * @throws IllegalArgumentException if the direction is missing
     */
    public void setDirection(GateDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Gate direction is required");
        }
        this.direction = direction;
    }
}
