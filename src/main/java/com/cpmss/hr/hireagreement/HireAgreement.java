package com.cpmss.hr.hireagreement;

import com.cpmss.hr.application.ApplicationId;
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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detail entity (1:1 extension of Application) recording hire terms for a successful applicant.
 *
 * <p>Composite PK matches Application: ({@code applicant_id},
 * {@code position_id}, {@code application_date}).
 * Only successful applicants get a row here.
 */
@Entity
@Table(name = "Hire_Agreement")
@IdClass(ApplicationId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HireAgreement extends BaseAuditEntity {

    /** The applicant (part of composite PK, FK to Application). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Person applicant;

    /** The position (part of composite PK, FK to Application). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private StaffPosition position;

    /** The application date (part of composite PK, FK to Application). */
    @Id
    @Column(name = "application_date")
    private LocalDate applicationDate;

    /** Agreed employment start date. */
    @Column(name = "employment_start_date")
    private LocalDate employmentStartDate;

    /** Agreed maximum monthly salary. */
    @Column(name = "offered_maximum_salary", precision = 12, scale = 2)
    private BigDecimal offeredMaximumSalary;

    /** Agreed base daily rate for pay calculation. */
    @Column(name = "offered_base_daily_rate", nullable = false, precision = 8, scale = 2)
    private BigDecimal offeredBaseDailyRate;
}
