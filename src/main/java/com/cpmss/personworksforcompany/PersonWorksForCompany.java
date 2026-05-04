package com.cpmss.personworksforcompany;

import com.cpmss.common.BaseAuditEntity;
import com.cpmss.company.Company;
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

/**
 * Junction entity recording which persons work for an external company (M:M).
 *
 * <p>Composite PK: ({@code employee_id}, {@code company_id}).
 * Full {@code @Entity} because the position within the company
 * is tracked as extra data.
 */
@Entity
@Table(name = "Person_Works_for_Company")
@IdClass(PersonWorksForCompanyId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonWorksForCompany extends BaseAuditEntity {

    /** The employee person (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Person employee;

    /** The employing company (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    /** The person's job title within the external company. */
    @Column(name = "position_in_company", length = 100)
    private String positionInCompany;
}
