package com.cpmss.finance.money;

import com.cpmss.platform.exception.BusinessException;
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
 * can embed this type without changing the existing {@code amount} and
 * {@code currency} table columns.
 *
 * @see com.cpmss.finance.payment.Payment
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money implements Serializable {

    /** Currency used when a legacy request omits the currency value. */
    public static final String DEFAULT_CURRENCY = "USD";

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
     * @throws BusinessException if the amount is missing or negative, or if
     *                           the currency is missing or invalid
     */
    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessException("Money amount is required");
        }
        if (amount.signum() < 0) {
            throw new BusinessException("Money amount cannot be negative");
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
     * @throws BusinessException if the amount is missing, zero, or negative,
     *                           or if the currency is missing or invalid
     */
    public static Money positive(BigDecimal amount, String currency) {
        Money money = new Money(amount, currency);
        if (money.amount.signum() <= 0) {
            throw new BusinessException("Money amount must be positive");
        }
        return money;
    }

    /**
     * Creates positive money while preserving the legacy default currency.
     *
     * <p>Existing payment requests allow {@code currency} to be omitted. This
     * factory keeps that API shape stable by substituting
     * {@link #DEFAULT_CURRENCY} only when the request value is {@code null}.
     *
     * @param amount the monetary amount; must be greater than zero
     * @param currency the optional ISO-4217 currency code
     * @return a normalized positive money value
     * @throws BusinessException if the amount is missing, zero, or negative,
     *                           or if the currency is blank or invalid
     */
    public static Money positiveOrDefaultCurrency(BigDecimal amount, String currency) {
        return positive(amount, currency != null ? currency : DEFAULT_CURRENCY);
    }

    /**
     * Adds another money value with the same currency.
     *
     * @param other the money value to add
     * @return a new money value containing the combined amount
     * @throws BusinessException if {@code other} is missing or uses a
     *                           different currency
     */
    public Money add(Money other) {
        if (other == null) {
            throw new BusinessException("Money to add is required");
        }
        if (!currency.equals(other.currency)) {
            throw new BusinessException("Cannot add money with different currencies");
        }
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Normalizes and validates an ISO-4217 currency code.
     *
     * @param currency the currency code supplied by an API request or entity
     * @return the uppercase ISO-4217 currency code
     * @throws BusinessException if the currency is missing, blank, or not a
     *                           valid ISO-4217 code
     */
    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new BusinessException("Money currency is required");
        }

        String normalized = currency.strip().toUpperCase(Locale.ROOT);
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Money currency must be a valid ISO-4217 code");
        }
        return normalized;
    }
}
