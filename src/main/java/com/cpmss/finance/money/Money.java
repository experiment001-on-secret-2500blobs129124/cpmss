package com.cpmss.finance.money;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Monetary amount with a normalized ISO-4217 currency code.
 *
 * <p>Centralizes the low-level money invariants used by finance workflows:
 * the amount must be present, the currency must be a real ISO-4217 code, and
 * arithmetic is only allowed between values with the same currency. Entities
 * embed this type over explicit amount and currency column pairs.
 *
 * @see com.cpmss.finance.payment.Payment
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Monetary quantity stored in the owning table's amount column. */
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** ISO-4217 currency code stored in the owning table's currency column. */
    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    /**
     * Creates a non-negative money value.
     *
     * <p>Use {@link #positive(BigDecimal, String)} when a workflow requires
     * strictly positive money, such as creating a {@code Payment}. Zero is
     * allowed here so the same value object can later model non-negative
     * values such as waived late fees or zero balances.
     *
     * @param amount the monetary amount; must be zero or greater
     * @param currency the ISO-4217 currency code
     * @throws ApiException if the amount is missing or negative, or if
     *                      the currency is missing or invalid
     */
    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount,
                 @JsonProperty("currency") String currency) {
        if (amount == null) {
            throw new ApiException(FinanceErrorCode.MONEY_AMOUNT_REQUIRED);
        }
        if (amount.signum() < 0) {
            throw new ApiException(FinanceErrorCode.MONEY_AMOUNT_NEGATIVE);
        }
        this.amount = amount;
        this.currency = normalizeCurrency(currency);
    }

    /**
     * Creates a strictly positive money value.
     *
     * <p>Use this factory for financial ledger movements where a zero amount
     * is invalid by business rule and by the database check constraint.
     *
     * @param amount the monetary amount; must be greater than zero
     * @param currency the ISO-4217 currency code
     * @return a normalized money value
     * @throws ApiException if the amount is missing, zero, or negative,
     *                      or if the currency is missing or invalid
     */
    public static Money positive(BigDecimal amount, String currency) {
        Money money = new Money(amount, currency);
        if (money.amount.signum() <= 0) {
            throw new ApiException(FinanceErrorCode.MONEY_AMOUNT_NOT_POSITIVE);
        }
        return money;
    }

    /**
     * Adds another money value with the same currency.
     *
     * @param other the money value to add
     * @return a new money value containing the combined amount
     * @throws ApiException if {@code other} is missing or uses a
     *                      different currency
     */
    public Money add(Money other) {
        if (other == null) {
            throw new ApiException(FinanceErrorCode.MONEY_ADD_REQUIRED);
        }
        if (!currency.equals(other.currency)) {
            throw new ApiException(FinanceErrorCode.MONEY_CURRENCY_MISMATCH);
        }
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Normalizes and validates an ISO-4217 currency code.
     *
     * @param currency the currency code supplied by an API request or entity
     * @return the uppercase ISO-4217 currency code
     * @throws ApiException if the currency is missing, blank, or not a
     *                      valid ISO-4217 code
     */
    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new ApiException(FinanceErrorCode.MONEY_CURRENCY_REQUIRED);
        }

        String normalized = currency.strip().toUpperCase(Locale.ROOT);
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ApiException(FinanceErrorCode.MONEY_CURRENCY_INVALID);
        }
        return normalized;
    }
}
