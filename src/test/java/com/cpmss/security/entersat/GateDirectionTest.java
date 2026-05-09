package com.cpmss.security.entersat;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GateDirectionTest {

    @Test
    void parsesDirectionLabels() {
        assertThat(GateDirection.fromLabel("In")).isEqualTo(GateDirection.IN);
        assertThat(GateDirection.fromLabel("Out")).isEqualTo(GateDirection.OUT);
    }

    @Test
    void rejectsUnknownDirection() {
        assertThatThrownBy(() -> GateDirection.fromLabel("Inside"))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.GATE_DIRECTION_INVALID);
                    assertThat(ex).hasMessage("Gate direction is not allowed");
                });
    }
}
