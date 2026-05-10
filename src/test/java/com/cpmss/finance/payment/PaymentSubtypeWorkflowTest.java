package com.cpmss.finance.payment;

import com.cpmss.finance.bankaccount.BankAccountRepository;
import com.cpmss.finance.installmentpayment.InstallmentPaymentRepository;
import com.cpmss.finance.installmentpayment.dto.CreateInstallmentPaymentRequest;
import com.cpmss.finance.money.Money;
import com.cpmss.finance.payment.dto.CreatePaymentRequest;
import com.cpmss.finance.payrollpayment.PayrollPaymentRepository;
import com.cpmss.finance.workorderpayment.WorkOrderPaymentRepository;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.leasing.installment.InstallmentRepository;
import com.cpmss.maintenance.workorder.WorkOrderRepository;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentSubtypeWorkflowTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InstallmentPaymentRepository installmentPaymentRepository;

    @Mock
    private WorkOrderPaymentRepository workOrderPaymentRepository;

    @Mock
    private PayrollPaymentRepository payrollPaymentRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private InstallmentRepository installmentRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TaskMonthlySalaryRepository taskMonthlySalaryRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void installmentPaymentRejectsOutboundDirection() {
        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), UUID.randomUUID(), SystemRole.ACCOUNTANT, "acc@example.com"));
        PaymentService service = new PaymentService(
                paymentRepository,
                installmentPaymentRepository,
                workOrderPaymentRepository,
                payrollPaymentRepository,
                bankAccountRepository,
                personRepository,
                installmentRepository,
                workOrderRepository,
                departmentRepository,
                taskMonthlySalaryRepository,
                currentUserService);
        CreatePaymentRequest payment = new CreatePaymentRequest(
                new PaymentNumber("PAY-100"),
                new Money(new BigDecimal("100.00"), "EGP"),
                PaymentType.INSTALLMENT.label(),
                PaymentMethod.CASH.label(),
                PaymentDirection.OUTBOUND.label(),
                null,
                UUID.randomUUID(),
                UUID.randomUUID());

        assertThatThrownBy(() -> service.createInstallmentPayment(
                new CreateInstallmentPaymentRequest(payment, UUID.randomUUID(), null)))
                .isInstanceOf(ApiException.class);
    }
}
