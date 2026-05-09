package com.cpmss.finance.bankaccount;

import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountReferenceValueTest {

    @Test
    void ibanNormalizesWhitespaceAndValidatesChecksum() {
        Iban iban = new Iban("gb82 west 1234 5698 7654 32");

        assertThat(iban.value()).isEqualTo("GB82WEST12345698765432");
    }

    @Test
    void ibanRejectsInvalidChecksum() {
        assertThatThrownBy(() -> new Iban("GB00WEST12345698765432"))
                .isInstanceOf(ApiException.class)
                .hasMessage("IBAN checksum is invalid");
    }

    @Test
    void ibanRejectsInvalidFormat() {
        assertThatThrownBy(() -> new Iban("1234WEST"))
                .isInstanceOf(ApiException.class)
                .hasMessage("IBAN format is invalid");
    }

    @Test
    void swiftCodeNormalizesAndValidatesBicShape() {
        SwiftCode swiftCode = new SwiftCode("deut de ff 500");

        assertThat(swiftCode.value()).isEqualTo("DEUTDEFF500");
    }

    @Test
    void swiftCodeRejectsMalformedValues() {
        assertThatThrownBy(() -> new SwiftCode("DEUT123"))
                .isInstanceOf(ApiException.class)
                .hasMessage("SWIFT/BIC code format is invalid");
    }

    @Test
    void optionalBankReferencesTreatBlankAsAbsent() {
        assertThat(Iban.optional(" ")).isNull();
        assertThat(SwiftCode.optional(" ")).isNull();
    }

    @Test
    void convertersPreserveNormalizedBankReferenceValues() {
        IbanConverter ibanConverter = new IbanConverter();
        SwiftCodeConverter swiftConverter = new SwiftCodeConverter();

        assertThat(ibanConverter.convertToDatabaseColumn(new Iban("gb82 west 1234 5698 7654 32")))
                .isEqualTo("GB82WEST12345698765432");
        assertThat(swiftConverter.convertToDatabaseColumn(new SwiftCode("deutdeff500")))
                .isEqualTo("DEUTDEFF500");
    }
}
