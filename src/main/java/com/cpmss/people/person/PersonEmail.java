package com.cpmss.people.person;

import com.cpmss.people.common.EmailAddress;
import com.cpmss.people.common.EmailAddressConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
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

    /** Contact email address mapped to the existing email column. */
    @Convert(converter = EmailAddressConverter.class)
    @Column(name = "email", nullable = false, length = 150)
    @Setter(AccessLevel.NONE)
    private EmailAddress email;

    /**
     * Creates a person contact email.
     *
     * @param email the raw email address
     * @throws com.cpmss.platform.exception.BusinessException if the email is
     *                                                        missing, too long,
     *                                                        or invalid
     */
    public PersonEmail(String email) {
        this.email = EmailAddress.contact(email);
    }

    /**
     * Returns the contact email for DTO compatibility.
     *
     * @return the normalized email address
     */
    public String getEmail() {
        return email != null ? email.value() : null;
    }

    /**
     * Returns the typed contact email for domain logic.
     *
     * @return the typed email address, or {@code null} when unset
     */
    public EmailAddress getEmailValue() {
        return email;
    }

    /**
     * Assigns the typed contact email.
     *
     * @param email the typed contact email
     * @throws IllegalArgumentException if the email is missing
     */
    public void setEmail(EmailAddress email) {
        if (email == null) {
            throw new IllegalArgumentException("Person email is required");
        }
        this.email = email;
    }
}
