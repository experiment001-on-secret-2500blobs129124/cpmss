package com.cpmss.people.person;

import com.cpmss.people.common.PhoneNumber;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
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

    /** Phone number mapped to the existing country_code and phone columns. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "countryCode",
                    column = @Column(name = "country_code", nullable = false, length = 5)),
            @AttributeOverride(name = "phone",
                    column = @Column(name = "phone", nullable = false, length = 20))
    })
    @Setter(AccessLevel.NONE)
    private PhoneNumber phoneNumber;

    /**
     * Creates a person phone number.
     *
     * @param countryCode the country dialing code
     * @param phone the phone number
     * @throws com.cpmss.platform.exception.ApiException if either value is
     *                                                        missing or too long
     */
    public PersonPhone(String countryCode, String phone) {
        this.phoneNumber = new PhoneNumber(countryCode, phone);
    }

    /**
     * Returns the country code for DTO compatibility.
     *
     * @return the country dialing code, or {@code null} when unset
     */
    public String getCountryCode() {
        return phoneNumber != null ? phoneNumber.getCountryCode() : null;
    }

    /**
     * Returns the phone number for DTO compatibility.
     *
     * @return the phone number, or {@code null} when unset
     */
    public String getPhone() {
        return phoneNumber != null ? phoneNumber.getPhone() : null;
    }

    /**
     * Returns the typed phone number for domain logic.
     *
     * @return the typed phone number, or {@code null} when unset
     */
    public PhoneNumber getPhoneNumberValue() {
        return phoneNumber;
    }

    /**
     * Assigns the typed phone number.
     *
     * @param phoneNumber the typed phone number
     * @throws IllegalArgumentException if the phone number is missing
     */
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Person phone number is required");
        }
        this.phoneNumber = phoneNumber;
    }
}
