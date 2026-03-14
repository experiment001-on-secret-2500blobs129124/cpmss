package com.cpmss.entrylog;

import com.cpmss.accesspermit.AccessPermit;
import com.cpmss.common.BaseImmutableEntity;
import com.cpmss.gate.Gate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entry log is immutable — once recorded, an entry event cannot be modified or deleted.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "entry_log")
public class EntryLog extends BaseImmutableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permit_id", nullable = false)
    private AccessPermit permit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gate_id", nullable = false)
    private Gate gate;

    @NotNull
    @Column(name = "entry_timestamp", nullable = false)
    private LocalDateTime entryTimestamp;

    @NotNull
    @Column(name = "direction", nullable = false)
    private String direction;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "manual_plate_entry")
    private String manualPlateEntry;
}
