package com.cpmss.personsupervision;

import com.cpmss.common.BaseAuditEntity;
import com.cpmss.person.Person;
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
 * Junction entity tracking supervisor-supervisee relationships (self-referential M:M).
 *
 * <p>Composite PK: ({@code supervisor_id}, {@code supervisee_id},
 * {@code supervision_start_date}). A person cannot supervise themselves —
 * enforced by CHECK constraint ({@code chk_no_self_supervision}) and
 * in {@link PersonSupervisionRules}.
 */
@Entity
@Table(name = "Person_Supervision")
@IdClass(PersonSupervisionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonSupervision extends BaseAuditEntity {

    /** The supervising person (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Person supervisor;

    /** The supervised person (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisee_id")
    private Person supervisee;

    /** The date this supervision relationship started (part of composite PK). */
    @Id
    @Column(name = "supervision_start_date")
    private LocalDate supervisionStartDate;

    /** The date this supervision ended ({@code null} = still active). */
    @Column(name = "supervision_end_date")
    private LocalDate supervisionEndDate;

    /** Optional team name grouping supervisees (e.g. "Alpha Team", "Night Shift"). */
    @Column(name = "team_name", length = 100)
    private String teamName;
}
