package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalTimeWindowTest {

    @Test
    void calculatesWindowDuration() {
        LocalTimeWindow window = new LocalTimeWindow(LocalTime.of(8, 30), LocalTime.of(17, 0));

        assertThat(window.duration()).isEqualTo(Duration.ofHours(8).plusMinutes(30));
    }

    @Test
    void rejectsNonPositiveWindow() {
        assertThatThrownBy(() -> new LocalTimeWindow(LocalTime.NOON, LocalTime.NOON))
                .isInstanceOf(BusinessException.class)
                .hasMessage("End time must be after start time");
    }
}
