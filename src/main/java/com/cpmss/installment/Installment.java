package com.cpmss.installment;

import com.cpmss.common.BaseEntity;
import com.cpmss.contract.Contract;
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
@Table(name = "installment")
public class Installment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Column(name = "amount_expected", nullable = false)
    private BigDecimal amountExpected;

    @Column(name = "status", nullable = false)
    private String status = "Pending";

    @Column(name = "installment_type")
    private String installmentType;
}
