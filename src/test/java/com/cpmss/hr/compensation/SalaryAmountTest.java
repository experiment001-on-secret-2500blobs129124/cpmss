package com.cpmss.hr.compensation;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SalaryAmountTest {

    @Test
    void acceptsPositiveSalaryAmount() {
        SalaryAmount amount = SalaryAmount.positive(new BigDecimal("250.00"));

        assertThat(amount.amount()).isEqualByComparingTo("250.00");
    }

    @Test
    void keepsNullableSalaryAbsent() {
        assertThat(SalaryAmount.nullablePositive(null)).isNull();
    }

    @Test
    void rejectsZeroSalaryAmount() {
        assertThatThrownBy(() -> SalaryAmount.positive(BigDecimal.ZERO))
                .isInstanceOf(ApiException.class)
                .hasMessage("Salary amount must be positive");
    }

    @Test
    void converterPreservesDecimalAmount() {
        SalaryAmountConverter converter = new SalaryAmountConverter();

        assertThat(converter.convertToDatabaseColumn(SalaryAmount.positive(new BigDecimal("10.50"))))
                .isEqualByComparingTo("10.50");
    }
}
