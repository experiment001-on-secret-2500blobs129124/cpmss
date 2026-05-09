package com.cpmss.property.common;

import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertyValueTest {

    @Test
    void acceptsPositiveArea() {
        Area area = new Area(new BigDecimal("120.50"));

        assertThat(area.value()).isEqualByComparingTo("120.50");
    }

    @Test
    void rejectsNonPositiveArea() {
        assertThatThrownBy(() -> new Area(BigDecimal.ZERO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Area must be positive");
    }

    @Test
    void acceptsZeroAsNonNegativeCount() {
        NonNegativeCount count = new NonNegativeCount(0);

        assertThat(count.value()).isZero();
    }

    @Test
    void rejectsNegativeCount() {
        assertThatThrownBy(() -> new NonNegativeCount(-1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Count cannot be negative");
    }

    @Test
    void acceptsOperatingHoursWithClosingAfterOpening() {
        OperatingHours hours = new OperatingHours(
                LocalTime.of(8, 0),
                LocalTime.of(17, 0));

        assertThat(hours.getOpeningTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(hours.getClosingTime()).isEqualTo(LocalTime.of(17, 0));
    }

    @Test
    void rejectsIncompleteOperatingHours() {
        assertThatThrownBy(() -> new OperatingHours(LocalTime.of(8, 0), null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Opening and closing time must be set together");
    }

    @Test
    void rejectsOperatingHoursWithoutLaterClosingTime() {
        assertThatThrownBy(() -> new OperatingHours(
                LocalTime.of(8, 0),
                LocalTime.of(8, 0)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Closing time must be after opening time");
    }

    @Test
    void parsesPropertyVocabularyLabels() {
        assertThat(BuildingType.fromLabel("Residential")).isEqualTo(BuildingType.RESIDENTIAL);
        assertThat(BuildingType.fromLabel("Non-Residential")).isEqualTo(BuildingType.NON_RESIDENTIAL);
        assertThat(FacilityManagementType.fromLabel("Compound")).isEqualTo(FacilityManagementType.COMPOUND);
        assertThat(FacilityManagementType.fromLabel("Vendor")).isEqualTo(FacilityManagementType.VENDOR);
        assertThat(UnitStatus.fromLabel("Vacant")).isEqualTo(UnitStatus.VACANT);
        assertThat(UnitStatus.fromLabel("Under Maintenance")).isEqualTo(UnitStatus.UNDER_MAINTENANCE);
    }

    @Test
    void rejectsUnknownPropertyVocabularyLabels() {
        assertThatThrownBy(() -> BuildingType.fromLabel("Mixed"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Building type must be one of: Residential, Non-Residential");
        assertThatThrownBy(() -> UnitStatus.fromLabel("Blocked"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Unit status must be one of: Vacant, Occupied, Under Maintenance, Reserved");
    }

    @Test
    void convertersPreserveDatabaseValues() {
        assertThat(new AreaConverter().convertToDatabaseColumn(new Area(new BigDecimal("88.75"))))
                .isEqualByComparingTo("88.75");
        assertThat(new NonNegativeCountConverter().convertToDatabaseColumn(new NonNegativeCount(3)))
                .isEqualTo(3);
        assertThat(new BuildingTypeConverter().convertToDatabaseColumn(BuildingType.NON_RESIDENTIAL))
                .isEqualTo("Non-Residential");
        assertThat(new FacilityManagementTypeConverter().convertToDatabaseColumn(FacilityManagementType.VENDOR))
                .isEqualTo("Vendor");
        assertThat(new UnitStatusConverter().convertToDatabaseColumn(UnitStatus.RESERVED))
                .isEqualTo("Reserved");
    }
}
