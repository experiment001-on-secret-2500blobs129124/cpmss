package com.cpmss.accesspermit;

import com.cpmss.common.BaseEntity;
import com.cpmss.person.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "access_permit")
public class AccessPermit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_id")
    private Person holder;

    @Column(name = "access_level")
    private String accessLevel;

    @Column(name = "status", nullable = false)
    private String status = "Active";

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "permit_type")
    private String permitType;
}
