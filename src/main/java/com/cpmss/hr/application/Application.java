package com.cpmss.hr.application;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.people.person.Person;
import com.cpmss.hr.staffposition.StaffPosition;
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
 * Core entity representing a job application — the root of the hiring pipeline.
 *
 * <p>Composite PK: ({@code applicant_id}, {@code position_id},
 * {@code application_date}). Application status is DERIVED — not stored.
 * If Recruitment rows exist → "Interviewing"; if Hire_Agreement → "Hired".
 */
@Entity
@Table(name = "Applications")
@IdClass(ApplicationId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseAuditEntity {

    /** The person applying (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Person applicant;

    /** The position applied for (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private StaffPosition position;

    /** The date of the application (part of composite PK). */
    @Id
    @Column(name = "application_date")
    private LocalDate applicationDate;
}
