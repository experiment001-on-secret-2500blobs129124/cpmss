package com.cpmss.finance.payment;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentReferenceValueTest {

    @Test
    void paymentNumberNormalizesAndAllowsReferenceSeparators() {
        PaymentNumber number = new PaymentNumber(" pay-2026/001 ");

        assertThat(number.value()).isEqualTo("PAY-2026/001");
    }

    @Test
    void paymentNumberRejectsUnsupportedCharacters() {
        assertThatThrownBy(() -> new PaymentNumber("PAY 2026"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Payment number format is invalid");
    }

    @Test
    void paymentNumberRejectsOverlongValues() {
        assertThatThrownBy(() -> new PaymentNumber("PAY-12345678901234567890"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Payment number must be at most 20 characters");
    }

    @Test
    void paymentReferenceTrimsOptionalExternalReference() {
        PaymentReference reference = new PaymentReference("  bank-transfer-7788  ");

        assertThat(reference.value()).isEqualTo("bank-transfer-7788");
    }

    @Test
    void paymentReferenceRejectsBlankValues() {
        assertThatThrownBy(() -> new PaymentReference(" "))
                .isInstanceOf(ApiException.class)
                .hasMessage("Payment reference is required");
    }

    @Test
    void optionalPaymentReferenceTreatsBlankAsAbsent() {
        assertThat(PaymentReference.optional(null)).isNull();
        assertThat(PaymentReference.optional(" ")).isNull();
    }

    @Test
    void convertersPreservePaymentReferenceValues() {
        PaymentNumberConverter numberConverter = new PaymentNumberConverter();
        PaymentReferenceConverter referenceConverter = new PaymentReferenceConverter();

        assertThat(numberConverter.convertToDatabaseColumn(new PaymentNumber("pay-2026/001")))
                .isEqualTo("PAY-2026/001");
        assertThat(referenceConverter.convertToDatabaseColumn(new PaymentReference(" ext-ref-5 ")))
                .isEqualTo("ext-ref-5");
    }
}
