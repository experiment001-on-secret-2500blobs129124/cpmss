package com.cpmss.hr.hireagreement;

import com.cpmss.hr.application.ApplicationId;
import com.cpmss.hr.compensation.SalaryAmount;
import com.cpmss.hr.compensation.SalaryAmountConverter;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.people.person.Person;
import com.cpmss.hr.staffposition.StaffPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    @JoinColumn(name = "applicant_id", nullable = false)
    private Person applicant;

    /** The position (part of composite PK, FK to Application). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private StaffPosition position;

    /** The application date (part of composite PK, FK to Application). */
    @Id
    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    /** Agreed employment start date. */
    @Column(name = "employment_start_date")
    private LocalDate employmentStartDate;

    /** Agreed maximum monthly salary. */
    @Convert(converter = SalaryAmountConverter.class)
    @Column(name = "offered_maximum_salary", precision = 12, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private SalaryAmount offeredMaximumSalary;

    /** Agreed base daily rate for pay calculation. */
    @Convert(converter = SalaryAmountConverter.class)
    @Column(name = "offered_base_daily_rate", nullable = false, precision = 8, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private SalaryAmount offeredBaseDailyRate;

    /**
     * Returns the offered maximum salary amount for DTO compatibility.
     *
     * @return the offered maximum salary, or {@code null} when absent
     */
    public BigDecimal getOfferedMaximumSalary() {
        return offeredMaximumSalary != null ? offeredMaximumSalary.amount() : null;
    }

    /**
     * Returns the typed offered maximum salary for domain logic.
     *
     * @return the typed offered maximum salary, or {@code null} when absent
     */
    public SalaryAmount getOfferedMaximumSalaryValue() {
        return offeredMaximumSalary;
    }

    /**
     * Returns the offered base daily rate amount for DTO compatibility.
     *
     * @return the offered base daily rate, or {@code null} when unset
     */
    public BigDecimal getOfferedBaseDailyRate() {
        return offeredBaseDailyRate != null ? offeredBaseDailyRate.amount() : null;
    }

    /**
     * Returns the typed offered base daily rate for domain logic.
     *
     * @return the typed offered base daily rate, or {@code null} when unset
     */
    public SalaryAmount getOfferedBaseDailyRateValue() {
        return offeredBaseDailyRate;
    }

    /**
     * Assigns the optional offered maximum salary.
     *
     * @param offeredMaximumSalary the optional offered maximum salary
     */
    public void setOfferedMaximumSalary(BigDecimal offeredMaximumSalary) {
        this.offeredMaximumSalary = SalaryAmount.nullablePositive(offeredMaximumSalary);
    }

    /**
     * Assigns the required offered base daily rate.
     *
     * @param offeredBaseDailyRate the offered base daily rate
     */
    public void setOfferedBaseDailyRate(BigDecimal offeredBaseDailyRate) {
        this.offeredBaseDailyRate = SalaryAmount.positive(offeredBaseDailyRate);
    }
}
