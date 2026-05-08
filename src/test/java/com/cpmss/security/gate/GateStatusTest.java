package com.cpmss.security.gate;

import com.cpmss.platform.exception.BusinessException;
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
                .isInstanceOf(BusinessException.class)
                .hasMessage("Gate status must be one of: Active, Under Maintenance, Closed");
    }
}
