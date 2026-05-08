package com.cpmss.people.person;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable multi-value component: a single phone number for a Person.
 *
 * <p>Mapped to the {@code Person_Phone} collection table.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonPhone {

    @Column(name = "country_code", nullable = false, length = 5)
    private String countryCode;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
}
