package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YearMonthPeriodTest {

    @Test
    void calculatesMonthBoundaries() {
        YearMonthPeriod period = YearMonthPeriod.of(2026, 2);

        assertThat(period.firstDay()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(period.lastDay()).isEqualTo(LocalDate.of(2026, 2, 28));
        assertThat(period.yearMonth()).isEqualTo(YearMonth.of(2026, 2));
    }

    @Test
    void rejectsInvalidMonth() {
        assertThatThrownBy(() -> YearMonthPeriod.of(2026, 13))
                .isInstanceOf(ApiException.class)
                .hasMessage("Year-month period is invalid");
    }
}
