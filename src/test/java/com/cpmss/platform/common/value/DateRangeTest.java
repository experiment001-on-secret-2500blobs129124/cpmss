package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeTest {

    @Test
    void acceptsOpenEndedRange() {
        DateRange range = new DateRange(LocalDate.of(2026, 1, 1), null);

        assertThat(range.contains(LocalDate.of(2026, 12, 31))).isTrue();
    }

    @Test
    void checksInclusiveBoundaries() {
        DateRange range = new DateRange(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertThat(range.contains(LocalDate.of(2026, 1, 1))).isTrue();
        assertThat(range.contains(LocalDate.of(2026, 1, 31))).isTrue();
        assertThat(range.contains(LocalDate.of(2026, 2, 1))).isFalse();
    }

    @Test
    void rejectsEndBeforeStart() {
        assertThatThrownBy(() -> new DateRange(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 31)))
                .isInstanceOf(ApiException.class)
                .hasMessage("End date cannot be before start date");
    }
}
