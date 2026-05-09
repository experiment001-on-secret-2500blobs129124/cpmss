package com.cpmss.finance.payment;

import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentVocabularyTest {

    @Test
    void parsesPaymentTypeLabels() {
        assertThat(PaymentType.fromLabel("Installment")).isEqualTo(PaymentType.INSTALLMENT);
        assertThat(PaymentType.fromLabel("WorkOrder")).isEqualTo(PaymentType.WORK_ORDER);
        assertThat(PaymentType.fromLabel("Payroll")).isEqualTo(PaymentType.PAYROLL);
    }

    @Test
    void parsesPaymentDirectionLabels() {
        assertThat(PaymentDirection.fromLabel("Inbound")).isEqualTo(PaymentDirection.INBOUND);
        assertThat(PaymentDirection.fromLabel("Outbound")).isEqualTo(PaymentDirection.OUTBOUND);
    }

    @Test
    void parsesNullablePaymentMethodLabels() {
        assertThat(PaymentMethod.fromNullableLabel(null)).isNull();
        assertThat(PaymentMethod.fromNullableLabel("Bank Transfer"))
                .isEqualTo(PaymentMethod.BANK_TRANSFER);
        assertThat(PaymentMethod.fromNullableLabel("Cheque")).isEqualTo(PaymentMethod.CHEQUE);
    }

    @Test
    void parsesReconciliationStatusLabels() {
        assertThat(ReconciliationStatus.fromLabel("Pending"))
                .isEqualTo(ReconciliationStatus.PENDING);
        assertThat(ReconciliationStatus.fromLabel("Reconciled"))
                .isEqualTo(ReconciliationStatus.RECONCILED);
        assertThat(ReconciliationStatus.fromLabel("Disputed"))
                .isEqualTo(ReconciliationStatus.DISPUTED);
    }

    @Test
    void rejectsUnknownPaymentVocabulary() {
        assertThatThrownBy(() -> PaymentType.fromLabel("Subscription"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment type must be one of: Installment, WorkOrder, Payroll");

        assertThatThrownBy(() -> PaymentMethod.fromNullableLabel("Wire"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment method must be one of: Cash, Bank Transfer, Cheque, Card, Other");
    }

    @Test
    void convertersPreserveDatabaseLabels() {
        assertThat(new PaymentTypeConverter().convertToDatabaseColumn(PaymentType.WORK_ORDER))
                .isEqualTo("WorkOrder");
        assertThat(new PaymentDirectionConverter().convertToDatabaseColumn(PaymentDirection.OUTBOUND))
                .isEqualTo("Outbound");
        assertThat(new PaymentMethodConverter().convertToDatabaseColumn(PaymentMethod.CARD))
                .isEqualTo("Card");
        assertThat(new ReconciliationStatusConverter()
                .convertToDatabaseColumn(ReconciliationStatus.RECONCILED))
                .isEqualTo("Reconciled");
    }
}
