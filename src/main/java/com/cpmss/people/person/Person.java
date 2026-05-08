package com.cpmss.people.person;

import com.cpmss.platform.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Core entity representing a natural person in the system.
 *
 * <p>A person is the basis for all roles, contracts, and relationships.
 * Phone numbers and email addresses are multi-value attributes stored
 * in separate collection tables.
 */
@Entity
@Table(name = "Person")
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person extends BaseEntity {

    /** 14-digit Egyptian national ID (nullable — Egyptians only). */
    @Column(name = "egyptian_national_id", unique = true, length = 14)
    private String egyptianNationalId;

    @Column(name = "passport_no", nullable = false, unique = true, length = 20)
    private String passportNo;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(length = 50)
    private String nationality;

    @Column(length = 50)
    private String city;

    @Column(length = 150)
    private String street;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 6)
    private String gender;

    @Column(name = "is_blacklisted", nullable = false)
    @Builder.Default
    private Boolean isBlacklisted = false;

    /** Multi-value: phone numbers. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "Person_Phone", joinColumns = @JoinColumn(name = "person_id"))
    @Builder.Default
    private Set<PersonPhone> phones = new HashSet<>();

    /** Multi-value: email addresses. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "Person_Email", joinColumns = @JoinColumn(name = "person_id"))
    @Builder.Default
    private Set<PersonEmail> emails = new HashSet<>();
}
