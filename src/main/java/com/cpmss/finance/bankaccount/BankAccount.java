package com.cpmss.finance.bankaccount;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.maintenance.company.Company;
import com.cpmss.property.compound.Compound;
import com.cpmss.people.person.Person;
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

/**
 * Owned entity representing a bank account belonging to exactly one owner.
 *
 * <p>Owner is exactly one of: a {@link Compound}, a {@link Person},
 * or a {@link Company}. Mutual exclusion is enforced in
 * {@link BankAccountRules} and by a CHECK constraint in V2.
 *
 * <p>Bank accounts are permanent financial records — never deleted.
 */
@Entity
@Table(name = "Bank_Account")
@AttributeOverride(name = "id", column = @Column(name = "account_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount extends BaseEntity {

    /** Bank institution name. */
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    /** International Bank Account Number. */
    @Convert(converter = IbanConverter.class)
    @Column(name = "iban", length = 34)
    private Iban iban;

    /** SWIFT/BIC code for international transfers. */
    @Convert(converter = SwiftCodeConverter.class)
    @Column(name = "swift_code", length = 11)
    private SwiftCode swiftCode;

    /** Whether this is the primary account for the owner. */
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    /** Compound owner (mutually exclusive with person and company). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    private Compound compound;

    /** Person owner (mutually exclusive with compound and company). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_owner_id")
    private Person accountOwner;

    /** Company owner (mutually exclusive with compound and person). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
