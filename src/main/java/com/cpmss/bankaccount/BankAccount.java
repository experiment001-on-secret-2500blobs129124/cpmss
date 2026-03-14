package com.cpmss.bankaccount;

import com.cpmss.common.BaseEntity;
import com.cpmss.company.Company;
import com.cpmss.department.Department;
import com.cpmss.person.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bank_account")
public class BankAccount extends BaseEntity {

    @NotBlank
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "iban")
    private String iban;

    @Column(name = "swift_code")
    private String swiftCode;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "owner_type")
    private String ownerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
