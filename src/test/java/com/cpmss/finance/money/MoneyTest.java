package com.cpmss.finance.money;

import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void normalizesCurrencyCode() {
        Money money = new Money(new BigDecimal("125.50"), " egp ");

        assertThat(money.getAmount()).isEqualByComparingTo("125.50");
        assertThat(money.getCurrency()).isEqualTo("EGP");
    }

    @Test
    void defaultsMissingCurrencyForPositiveMoney() {
        Money money = Money.positiveOrDefaultCurrency(new BigDecimal("10.00"), null);

        assertThat(money.getCurrency()).isEqualTo(Money.DEFAULT_CURRENCY);
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> new Money(new BigDecimal("-0.01"), "USD"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Money amount cannot be negative");
    }

    @Test
    void positiveFactoryRejectsZero() {
        assertThatThrownBy(() -> Money.positive(BigDecimal.ZERO, "USD"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Money amount must be positive");
    }

    @Test
    void rejectsInvalidCurrency() {
        assertThatThrownBy(() -> new Money(BigDecimal.ONE, "dollars"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Money currency must be a valid ISO-4217 code");
    }

    @Test
    void addsSameCurrencyMoney() {
        Money first = new Money(new BigDecimal("12.25"), "USD");
        Money second = new Money(new BigDecimal("7.75"), "usd");

        Money sum = first.add(second);

        assertThat(sum.getAmount()).isEqualByComparingTo("20.00");
        assertThat(sum.getCurrency()).isEqualTo("USD");
    }

    @Test
    void rejectsAddingDifferentCurrencies() {
        Money dollars = new Money(BigDecimal.ONE, "USD");
        Money pounds = new Money(BigDecimal.ONE, "EGP");

        assertThatThrownBy(() -> dollars.add(pounds))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot add money with different currencies");
    }
}
