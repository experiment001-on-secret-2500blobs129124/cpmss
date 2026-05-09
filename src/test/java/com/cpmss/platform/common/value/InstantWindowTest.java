package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InstantWindowTest {

    @Test
    void calculatesWindowDuration() {
        Instant start = Instant.parse("2026-05-08T08:00:00Z");
        Instant end = Instant.parse("2026-05-08T16:00:00Z");

        assertThat(new InstantWindow(start, end).duration()).isEqualTo(Duration.ofHours(8));
    }

    @Test
    void rejectsEndBeforeStart() {
        Instant start = Instant.parse("2026-05-08T16:00:00Z");
        Instant end = Instant.parse("2026-05-08T08:00:00Z");

        assertThatThrownBy(() -> new InstantWindow(start, end))
                .isInstanceOf(ApiException.class)
                .hasMessage("End instant must be after start instant");
    }
}
