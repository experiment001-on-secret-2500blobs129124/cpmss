package com.cpmss.maintenance.company;

import com.cpmss.platform.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Core entity representing an external company associated with the compound.
 *
 * <p>Also acts as a vendor — companies can be assigned to work orders
 * and can manage facilities.
 */
@Entity
@Table(name = "Company")
@AttributeOverride(name = "id", column = @Column(name = "company_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    /** Display name of the company. */
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    /** Tax identification number. */
    @Column(name = "tax_id", length = 50)
    private String taxId;

    /** Primary contact phone number. */
    @Column(name = "phone_no", length = 20)
    private String phoneNo;

    /** Company classification (e.g. Vendor, Contractor). */
    @Column(name = "company_type", length = 50)
    private String companyType;
}
