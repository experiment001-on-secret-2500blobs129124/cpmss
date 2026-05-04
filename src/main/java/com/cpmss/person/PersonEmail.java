package com.cpmss.person;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable multi-value component: a single email address for a Person.
 *
 * <p>Mapped to the {@code Person_Email} collection table.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonEmail {

    @Column(name = "email", nullable = false, length = 150)
    private String email;
}
