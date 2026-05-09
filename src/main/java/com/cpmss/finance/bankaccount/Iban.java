package com.cpmss.finance.bankaccount;

import com.cpmss.platform.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * International Bank Account Number used by bank-account records.
 *
 * <p>The value is normalized by removing whitespace and uppercasing before
 * validating the IBAN structure and ISO 7064 mod-97 checksum.
 *
 * @param value normalized IBAN text
 */
public record Iban(String value) {

    private static final int MAX_LENGTH = 34;

    /**
     * Creates a validated IBAN value.
     *
     * @throws BusinessException if the value is missing, malformed, too long,
     *                           or fails the IBAN checksum
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Iban {
        value = normalize(value);
        if (value.length() > MAX_LENGTH) {
            throw new BusinessException("IBAN must be at most 34 characters");
        }
        if (!value.matches("[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}")) {
            throw new BusinessException("IBAN format is invalid");
        }
        if (!hasValidChecksum(value)) {
            throw new BusinessException("IBAN checksum is invalid");
        }
    }

    /**
     * Creates an optional IBAN.
     *
     * @param value optional raw IBAN text
     * @return the IBAN value, or {@code null} when absent
     */
    public static Iban optional(String value) {
        return value == null || value.isBlank() ? null : new Iban(value);
    }

    /**
     * Serializes this value as normalized IBAN text.
     *
     * @return normalized IBAN text
     */
    @JsonValue
    public String toJson() {
        return value;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("IBAN is required");
        }
        return value.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private static boolean hasValidChecksum(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        int remainder = 0;
        for (int i = 0; i < rearranged.length(); i++) {
            char current = rearranged.charAt(i);
            String digits = Character.isDigit(current)
                    ? Character.toString(current)
                    : Integer.toString(current - 'A' + 10);
            for (int j = 0; j < digits.length(); j++) {
                remainder = (remainder * 10 + digits.charAt(j) - '0') % 97;
            }
        }
        return remainder == 1;
    }
}
