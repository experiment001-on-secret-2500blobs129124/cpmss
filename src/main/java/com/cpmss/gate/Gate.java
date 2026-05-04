package com.cpmss.gate;

import com.cpmss.common.BaseEntity;
import com.cpmss.compound.Compound;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Owned entity representing a physical access gate in a compound.
 *
 * <p>Gates are the entry/exit points for persons and vehicles.
 * Gate name must be unique within a compound (composite unique
 * constraint: compound_id + gate_name).
 */
@Entity
@Table(name = "Gate", uniqueConstraints =
        @UniqueConstraint(columnNames = {"compound_id", "gate_name"}))
@AttributeOverride(name = "id", column = @Column(name = "gate_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gate extends BaseEntity {

    /** Gate number — unique across the entire system. */
    @Column(name = "gate_no", nullable = false, unique = true, length = 20)
    private String gateNo;

    /** Display name of the gate (e.g. "Main Entrance"). */
    @Column(name = "gate_name", nullable = false, length = 100)
    private String gateName;

    /** Gate classification (e.g. Pedestrian, Vehicle, Mixed). */
    @Column(name = "gate_type", length = 50)
    private String gateType;

    /** Operational status (e.g. Active, Maintenance). */
    @Column(name = "gate_status", length = 50)
    private String gateStatus;

    /** The compound this gate belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private Compound compound;
}
