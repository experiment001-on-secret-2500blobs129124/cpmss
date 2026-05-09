package com.cpmss.people.person;

import com.cpmss.people.common.EgyptianNationalId;
import com.cpmss.people.common.EgyptianNationalIdConverter;
import com.cpmss.people.common.Gender;
import com.cpmss.people.common.GenderConverter;
import com.cpmss.people.common.PassportNumber;
import com.cpmss.people.common.PassportNumberConverter;
import com.cpmss.platform.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
@AttributeOverride(name = "id", column = @Column(name = "person_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person extends BaseEntity {

    /** 14-digit Egyptian national ID (nullable — Egyptians only). */
    @Convert(converter = EgyptianNationalIdConverter.class)
    @Column(name = "egyptian_national_id", unique = true, length = 14)
    @Setter(AccessLevel.NONE)
    private EgyptianNationalId egyptianNationalId;

    /** Required passport number, unique per person. */
    @Convert(converter = PassportNumberConverter.class)
    @Column(name = "passport_no", nullable = false, unique = true, length = 20)
    @Setter(AccessLevel.NONE)
    private PassportNumber passportNo;

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

    /** Gender label constrained by Flyway V2. */
    @Convert(converter = GenderConverter.class)
    @Column(length = 6)
    @Setter(AccessLevel.NONE)
    private Gender gender;

    @Column(name = "is_blacklisted", nullable = false)
    @Builder.Default
    private Boolean isBlacklisted = false;

    /** Multi-value: phone numbers. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "Person_Phone", joinColumns = @JoinColumn(name = "person_id", nullable = false))
    @Builder.Default
    private Set<PersonPhone> phones = new HashSet<>();

    /** Multi-value: email addresses. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "Person_Email", joinColumns = @JoinColumn(name = "person_id", nullable = false))
    @Builder.Default
    private Set<PersonEmail> emails = new HashSet<>();

    /**
     * Returns the passport number for DTO compatibility.
     *
     * @return the passport number, or {@code null} when unset
     */
    public String getPassportNo() {
        return passportNo != null ? passportNo.value() : null;
    }

    /**
     * Returns the typed passport number for domain logic.
     *
     * @return the typed passport number, or {@code null} when unset
     */
    public PassportNumber getPassportNoValue() {
        return passportNo;
    }

    /**
     * Returns the Egyptian national ID for DTO compatibility.
     *
     * @return the national ID, or {@code null} when absent
     */
    public String getEgyptianNationalId() {
        return egyptianNationalId != null ? egyptianNationalId.value() : null;
    }

    /**
     * Returns the typed Egyptian national ID for domain logic.
     *
     * @return the typed national ID, or {@code null} when absent
     */
    public EgyptianNationalId getEgyptianNationalIdValue() {
        return egyptianNationalId;
    }

    /**
     * Returns the gender label for DTO compatibility.
     *
     * @return the gender label, or {@code null} when absent
     */
    public String getGender() {
        return gender != null ? gender.label() : null;
    }

    /**
     * Returns the typed gender for domain logic.
     *
     * @return the typed gender, or {@code null} when absent
     */
    public Gender getGenderValue() {
        return gender;
    }

    /**
     * Assigns the required passport number.
     *
     * @param passportNo the typed passport number
     * @throws IllegalArgumentException if the passport number is missing
     */
    public void setPassportNo(PassportNumber passportNo) {
        if (passportNo == null) {
            throw new IllegalArgumentException("Passport number is required");
        }
        this.passportNo = passportNo;
    }

    /**
     * Assigns the optional Egyptian national ID.
     *
     * @param egyptianNationalId the typed Egyptian national ID
     */
    public void setEgyptianNationalId(EgyptianNationalId egyptianNationalId) {
        this.egyptianNationalId = egyptianNationalId;
    }

    /**
     * Assigns the optional gender.
     *
     * @param gender the typed gender
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
