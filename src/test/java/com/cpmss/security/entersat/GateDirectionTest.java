package com.cpmss.security.entersat;

import com.cpmss.platform.exception.BusinessException;
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
                .isInstanceOf(BusinessException.class)
                .hasMessage("Gate direction must be one of: In, Out");
    }
}
