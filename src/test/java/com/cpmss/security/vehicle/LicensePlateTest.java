package com.cpmss.security.vehicle;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LicensePlateTest {

    @Test
    void normalizesPlateValue() {
        LicensePlate plate = LicensePlate.of(" abc-123 ");

        assertThat(plate.value()).isEqualTo("ABC-123");
    }

    @Test
    void keepsNullablePlateAbsent() {
        assertThat(LicensePlate.ofNullable(null)).isNull();
    }

    @Test
    void rejectsBlankPlate() {
        assertThatThrownBy(() -> LicensePlate.of(" "))
                .isInstanceOf(ApiException.class)
                .hasMessage("License plate is required");
    }

    @Test
    void converterPreservesNormalizedValue() {
        LicensePlateConverter converter = new LicensePlateConverter();

        assertThat(converter.convertToDatabaseColumn(LicensePlate.of("xyz 789")))
                .isEqualTo("XYZ 789");
    }
}
