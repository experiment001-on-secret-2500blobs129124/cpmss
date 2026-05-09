package com.cpmss.security.gate;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GateStatusTest {

    @Test
    void parsesNullableStatusLabels() {
        assertThat(GateStatus.fromNullableLabel(null)).isNull();
        assertThat(GateStatus.fromNullableLabel("Under Maintenance"))
                .isEqualTo(GateStatus.UNDER_MAINTENANCE);
    }

    @Test
    void rejectsUnknownStatus() {
        assertThatThrownBy(() -> GateStatus.fromNullableLabel("Open"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.GATE_STATUS_INVALID);
                    assertThat(ex).hasMessage("Gate status is not allowed");
                });
    }
}
