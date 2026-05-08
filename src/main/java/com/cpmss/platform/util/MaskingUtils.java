package com.cpmss.platform.util;

/**
 * Masks sensitive fields before they appear in API responses.
 *
 * <p>Shows only the last 4 digits of national IDs and bank account numbers.
 */
public final class MaskingUtils {

    private MaskingUtils() {}

    /**
     * Masks a national ID, showing only the last 4 digits.
     *
     * <p>Example: {@code "30012345678901"} → {@code "**********8901"}.
     *
     * @param nationalId the full national ID string
     * @return masked string, or the original value if it is {@code null}
     *         or 4 characters or fewer
     */
    public static String maskNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() <= 4) {
            return nationalId;
        }
        return "*".repeat(nationalId.length() - 4) + nationalId.substring(nationalId.length() - 4);
    }

    /**
     * Masks a bank account number, showing only the last 4 digits.
     *
     * <p>Example: {@code "1234567890123456"} → {@code "************3456"}.
     *
     * @param accountNumber the full account number string
     * @return masked string, or the original value if it is {@code null}
     *         or 4 characters or fewer
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        return "*".repeat(accountNumber.length() - 4)
                + accountNumber.substring(accountNumber.length() - 4);
    }
}
