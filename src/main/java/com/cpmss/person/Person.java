package com.cpmss.person;

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
@Table(name = "person")
public class Person extends BaseEntity {

    @NotBlank
    @Column(name = "national_id", nullable = false, unique = true)
    private String nationalId;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "phone1_country")
    private String phone1Country;

    @Column(name = "phone1_number")
    private String phone1Number;

    @Column(name = "phone2_country")
    private String phone2Country;

    @Column(name = "phone2_number")
    private String phone2Number;

    @Column(name = "email1")
    private String email1;

    @Column(name = "email2")
    private String email2;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "person_type", nullable = false)
    private String personType = "Visitor";

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "is_blacklisted", nullable = false)
    private Boolean isBlacklisted = false;

    /**
     * Convenience: full name for display.
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
