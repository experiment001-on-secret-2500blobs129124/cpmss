package com.cpmss.people.person;

import com.cpmss.people.role.Role;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.time.Instant;

/**
 * Junction entity: assigns one or more roles to a person (M:M).
 *
 * <p>Full entity (not silent {@code @JoinTable}) because role assignments
 * are audited. Every person must have at least one role — enforced in
 * {@link PersonRules}.
 */
@Entity
@Table(name = "Person_Role")
@IdClass(PersonRoleId.class)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonRole {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 255, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    private String updatedBy;
}
