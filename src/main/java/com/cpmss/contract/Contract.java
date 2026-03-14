package com.cpmss.contract;

import com.cpmss.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contract")
public class Contract extends BaseEntity {

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "contract_type", nullable = false)
    private String contractType;

    @Column(name = "contract_status", nullable = false)
    private String contractStatus = "Draft";

    @Column(name = "payment_frequency")
    private String paymentFrequency;

    @NotNull
    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "security_deposit")
    private BigDecimal securityDeposit = BigDecimal.ZERO;

    @Column(name = "renewal_terms", columnDefinition = "TEXT")
    private String renewalTerms;
}
