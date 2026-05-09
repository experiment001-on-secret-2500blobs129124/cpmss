package com.cpmss.people.common;

import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeopleIdentityValueTest {

    @Test
    void normalizesEmailAddresses() {
        EmailAddress email = EmailAddress.of(" USER@Example.COM ");

        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void rejectsInvalidEmailAddresses() {
        assertThatThrownBy(() -> EmailAddress.of("user-at-example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email address is invalid");
    }

    @Test
    void appliesContactEmailLengthLimit() {
        String tooLong = "a".repeat(142) + "@example.com";

        assertThatThrownBy(() -> EmailAddress.contact(tooLong))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email address is too long");
    }

    @Test
    void trimsPhoneNumberParts() {
        PhoneNumber phoneNumber = new PhoneNumber(" +20 ", " 01000000000 ");

        assertThat(phoneNumber.getCountryCode()).isEqualTo("+20");
        assertThat(phoneNumber.getPhone()).isEqualTo("01000000000");
    }

    @Test
    void rejectsMissingPhoneNumberParts() {
        assertThatThrownBy(() -> new PhoneNumber(null, "01000000000"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Phone country code is required");
        assertThatThrownBy(() -> new PhoneNumber("+20", " "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Phone number is required");
    }

    @Test
    void validatesEgyptianNationalIdsByNationality() {
        EgyptianNationalId id = EgyptianNationalId.forNationality("Egyptian", "12345678901234");

        assertThat(id.value()).isEqualTo("12345678901234");
        assertThat(EgyptianNationalId.forNationality("German", null)).isNull();
    }

    @Test
    void rejectsInvalidEgyptianNationalIds() {
        assertThatThrownBy(() -> EgyptianNationalId.of("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Egyptian national ID must be 14 digits");
    }

    @Test
    void normalizesPassportNumbers() {
        PassportNumber passportNumber = PassportNumber.of(" A1234567 ");

        assertThat(passportNumber.value()).isEqualTo("A1234567");
    }

    @Test
    void rejectsInvalidPassportNumbers() {
        assertThatThrownBy(() -> PassportNumber.of(" "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Passport number is required");
        assertThatThrownBy(() -> PassportNumber.of("A".repeat(21)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Passport number is too long");
    }

    @Test
    void parsesGenderLabels() {
        assertThat(Gender.fromLabel("Male")).isEqualTo(Gender.MALE);
        assertThat(Gender.fromLabel("Female")).isEqualTo(Gender.FEMALE);
        assertThat(Gender.fromNullableLabel(null)).isNull();
    }

    @Test
    void rejectsUnknownGenderLabels() {
        assertThatThrownBy(() -> Gender.fromLabel("Other"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Gender must be one of: Male, Female");
    }

    @Test
    void convertersPreserveDatabaseValues() {
        assertThat(new EmailAddressConverter().convertToDatabaseColumn(EmailAddress.of("USER@EXAMPLE.COM")))
                .isEqualTo("user@example.com");
        assertThat(new PassportNumberConverter().convertToDatabaseColumn(PassportNumber.of(" A1234567 ")))
                .isEqualTo("A1234567");
        assertThat(new EgyptianNationalIdConverter()
                .convertToDatabaseColumn(EgyptianNationalId.of("12345678901234")))
                .isEqualTo("12345678901234");
        assertThat(new GenderConverter().convertToDatabaseColumn(Gender.FEMALE))
                .isEqualTo("Female");
    }
}
