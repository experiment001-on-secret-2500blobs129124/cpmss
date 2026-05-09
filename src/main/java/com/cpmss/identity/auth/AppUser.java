package com.cpmss.identity.auth;

import com.cpmss.people.common.EmailAddress;
import com.cpmss.people.common.EmailAddressConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
import java.util.UUID;

/**
 * JPA entity for the {@code App_User} table — system login account.
 *
 * <p>Does not extend {@link com.cpmss.platform.common.BaseEntity} because the
 * primary key column is {@code user_id}, not {@code id}. Audit fields
 * are mapped directly.
 *
 * <p>See DATABASE.md § "Role Architecture" for why this is separate
 * from {@code Person}.
 */
@Entity
@Table(name = "App_User")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID id;

    /** Login email, independent from Person_Email contact addresses. */
    @Convert(converter = EmailAddressConverter.class)
    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Setter(lombok.AccessLevel.NONE)
    private EmailAddress email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role", nullable = false, length = 50)
    private SystemRole systemRole;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "force_password_change", nullable = false)
    @Builder.Default
    private boolean forcePasswordChange = true;

    @Column(name = "person_id", unique = true)
    private UUID personId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns the login email for DTO, JWT, and repository compatibility.
     *
     * @return the normalized login email, or {@code null} when unset
     */
    public String getEmail() {
        return email != null ? email.value() : null;
    }

    /**
     * Returns the typed login email for domain logic.
     *
     * @return the typed login email, or {@code null} when unset
     */
    public EmailAddress getEmailValue() {
        return email;
    }

    /**
     * Assigns the login email from a raw string.
     *
     * @param email the raw login email
     * @throws com.cpmss.platform.exception.ApiException if the email is
     *                                                        missing, too long,
     *                                                        or invalid
     */
    public void setEmail(String email) {
        this.email = EmailAddress.of(email);
    }

    /**
     * Assigns the typed login email.
     *
     * @param email the typed login email
     * @throws IllegalArgumentException if the email is missing
     */
    public void setEmail(EmailAddress email) {
        if (email == null) {
            throw new IllegalArgumentException("Login email is required");
        }
        this.email = email;
    }
}
