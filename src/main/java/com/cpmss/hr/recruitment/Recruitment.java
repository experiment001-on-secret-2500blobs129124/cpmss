package com.cpmss.hr.recruitment;

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
 * Child entity recording individual interview events per application.
 *
 * <p>Composite PK: ({@code interviewer_id}, {@code applicant_id},
 * {@code position_id}, {@code application_date}, {@code interview_date}).
 * Composite FK to Applications: ({@code applicant_id}, {@code position_id},
 * {@code application_date}).
 */
@Entity
@Table(name = "Recruitment")
@IdClass(RecruitmentId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment extends BaseAuditEntity {

    /** The interviewer (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Person interviewer;

    /** The applicant (part of composite PK and FK to Application). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, insertable = false, updatable = false)
    private Person applicant;

    /** The position (part of composite PK and FK to Application). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false, insertable = false, updatable = false)
    private StaffPosition position;

    /** The application date (part of composite PK and FK to Application). */
    @Id
    @Column(name = "application_date", nullable = false, insertable = false, updatable = false)
    private LocalDate applicationDate;

    /** The interview date (part of composite PK). */
    @Id
    @Column(name = "interview_date", nullable = false)
    private LocalDate interviewDate;

    /** Interview outcome (Pass, Fail, Pending). */
    @Column(name = "interview_result", length = 20)
    private String interviewResult;
}
