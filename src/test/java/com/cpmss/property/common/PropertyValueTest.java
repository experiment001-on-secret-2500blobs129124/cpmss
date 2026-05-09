package com.cpmss.property.common;

import com.cpmss.platform.exception.ApiException;
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
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.AREA_NOT_POSITIVE);
                    assertThat(ex).hasMessage("Area must be positive");
                });
    }

    @Test
    void acceptsZeroAsNonNegativeCount() {
        NonNegativeCount count = new NonNegativeCount(0);

        assertThat(count.value()).isZero();
    }

    @Test
    void rejectsNegativeCount() {
        assertThatThrownBy(() -> new NonNegativeCount(-1))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.COUNT_NEGATIVE);
                    assertThat(ex).hasMessage("Count cannot be negative");
                });
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
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.OPERATING_HOURS_INCOMPLETE);
                    assertThat(ex).hasMessage("Opening and closing time must be set together");
                });
    }

    @Test
    void rejectsOperatingHoursWithoutLaterClosingTime() {
        assertThatThrownBy(() -> new OperatingHours(
                LocalTime.of(8, 0),
                LocalTime.of(8, 0)))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.OPERATING_HOURS_INVALID);
                    assertThat(ex).hasMessage("Closing time must be after opening time");
                });
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
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.BUILDING_TYPE_INVALID);
                    assertThat(ex).hasMessage("Building type is not allowed");
                });
        assertThatThrownBy(() -> FacilityManagementType.fromLabel("Hybrid"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.FACILITY_MGMT_TYPE_INVALID);
                    assertThat(ex).hasMessage("Facility management type is not allowed");
                });
        assertThatThrownBy(() -> UnitStatus.fromLabel("Blocked"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PropertyErrorCode.UNIT_STATUS_INVALID);
                    assertThat(ex).hasMessage("Unit status is not allowed");
                });
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
