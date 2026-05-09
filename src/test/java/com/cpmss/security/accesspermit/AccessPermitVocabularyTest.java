package com.cpmss.security.accesspermit;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessPermitVocabularyTest {

    @Test
    void parsesPermitVocabularyLabels() {
        assertThat(PermitType.fromLabel("Staff Badge")).isEqualTo(PermitType.STAFF_BADGE);
        assertThat(PermitType.fromLabel("Vehicle Sticker")).isEqualTo(PermitType.VEHICLE_STICKER);
        assertThat(AccessLevel.fromNullableLabel("Common Areas Only"))
                .isEqualTo(AccessLevel.COMMON_AREAS_ONLY);
        assertThat(PermitStatus.fromLabel("Revoked")).isEqualTo(PermitStatus.REVOKED);
    }

    @Test
    void keepsAccessLevelNullable() {
        assertThat(AccessLevel.fromNullableLabel(null)).isNull();
    }

    @Test
    void rejectsUnknownPermitVocabulary() {
        assertThatThrownBy(() -> PermitType.fromLabel("Temporary Badge"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.PERMIT_TYPE_INVALID);
                    assertThat(ex).hasMessage("Permit type is not allowed");
                });
        assertThatThrownBy(() -> AccessLevel.fromNullableLabel("VIP"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.ACCESS_LEVEL_INVALID);
                    assertThat(ex).hasMessage("Access level is not allowed");
                });
        assertThatThrownBy(() -> PermitStatus.fromLabel("Paused"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.PERMIT_STATUS_INVALID);
                    assertThat(ex).hasMessage("Permit status is not allowed");
                });
    }

    @Test
    void validatesPermitValidity() {
        PermitValidity validity = new PermitValidity(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31));

        assertThat(validity.contains(LocalDate.of(2026, 5, 8))).isTrue();
        assertThat(validity.contains(LocalDate.of(2026, 6, 1))).isFalse();
    }

    @Test
    void rejectsExpiryBeforeIssue() {
        assertThatThrownBy(() -> new PermitValidity(
                LocalDate.of(2026, 5, 8),
                LocalDate.of(2026, 5, 7)))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.PERMIT_DATE_RANGE_INVALID);
                    assertThat(ex).hasMessage("Permit expiry date cannot be before issue date");
                });
    }
}
