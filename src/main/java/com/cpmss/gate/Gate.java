package com.cpmss.gate;

import com.cpmss.common.BaseEntity;
import com.cpmss.compound.Compound;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "gate")
public class Gate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private Compound compound;

    @NotBlank
    @Column(name = "gate_name", nullable = false)
    private String gateName;

    @Column(name = "gate_type")
    private String gateType;

    @Column(name = "status", nullable = false)
    private String status = "Active";
}
