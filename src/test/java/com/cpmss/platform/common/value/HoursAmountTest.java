package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HoursAmountTest {

    @Test
    void acceptsPositiveHoursAmount() {
        HoursAmount amount = HoursAmount.positive(new BigDecimal("8.50"));

        assertThat(amount.hours()).isEqualByComparingTo("8.50");
    }

    @Test
    void rejectsNegativeHoursAmount() {
        assertThatThrownBy(() -> HoursAmount.positive(new BigDecimal("-1.00")))
                .isInstanceOf(ApiException.class)
                .hasMessage("Hours amount must be positive");
    }

    @Test
    void converterPreservesDecimalHours() {
        HoursAmountConverter converter = new HoursAmountConverter();

        assertThat(converter.convertToDatabaseColumn(HoursAmount.positive(new BigDecimal("7.25"))))
                .isEqualByComparingTo("7.25");
    }
}
