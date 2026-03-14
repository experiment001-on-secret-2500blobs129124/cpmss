package com.cpmss.company;

import com.cpmss.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @NotBlank
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "company_type")
    private String companyType;
}
