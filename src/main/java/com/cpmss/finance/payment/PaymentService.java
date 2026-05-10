package com.cpmss.finance.payment;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.finance.bankaccount.BankAccountRepository;
import com.cpmss.finance.common.FinanceAccessRules;
import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.leasing.installment.Installment;
import com.cpmss.leasing.installment.InstallmentRepository;
import com.cpmss.leasing.common.InstallmentStatus;
import com.cpmss.leasing.common.LeasingErrorCode;
import com.cpmss.finance.installmentpayment.InstallmentPayment;
import com.cpmss.finance.installmentpayment.InstallmentPaymentRepository;
import com.cpmss.finance.installmentpayment.dto.CreateInstallmentPaymentRequest;
import com.cpmss.finance.money.Money;
import com.cpmss.finance.payment.dto.CreatePaymentRequest;
import com.cpmss.finance.payment.dto.PaymentResponse;
import com.cpmss.finance.payrollpayment.PayrollPayment;
import com.cpmss.finance.payrollpayment.PayrollPaymentRepository;
import com.cpmss.finance.payrollpayment.dto.CreatePayrollPaymentRequest;
import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.maintenance.workorder.WorkOrder;
import com.cpmss.maintenance.workorder.WorkOrderRepository;
import com.cpmss.maintenance.workorder.WorkOrderStatus;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.finance.workorderpayment.WorkOrderPayment;
import com.cpmss.finance.workorderpayment.WorkOrderPaymentRepository;
import com.cpmss.finance.workorderpayment.dto.CreateWorkOrderPaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalary;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalaryRepository;

import java.time.Instant;
import java.util.UUID;

/**
 * Manages polymorphic payment creation and querying.
 *
 * <p>Each create method is @Transactional, creating a parent Payment
 * record and exactly one child record (InstallmentPayment,
 * WorkOrderPayment, or PayrollPayment). Parent payment money is validated as
 * one explicit {@link Money} value before the row is persisted.
 *
 * @see Money
 * @see PaymentRules
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final InstallmentPaymentRepository installmentPaymentRepository;
    private final WorkOrderPaymentRepository workOrderPaymentRepository;
    private final PayrollPaymentRepository payrollPaymentRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PersonRepository personRepository;
    private final InstallmentRepository installmentRepository;
    private final WorkOrderRepository workOrderRepository;
    private final DepartmentRepository departmentRepository;
    private final TaskMonthlySalaryRepository taskMonthlySalaryRepository;
    private final CurrentUserService currentUserService;
    private final PaymentRules rules = new PaymentRules();
    private final FinanceAccessRules accessRules = new FinanceAccessRules();

    /**
     * Creates the payment service with all repositories needed by subtype flows.
     *
     * <p>Each subtype workflow persists the same parent {@link Payment}
     * aggregate root plus exactly one subtype row, so the service owns the
     * repositories for all three subtype tables.
     *
     * @param paymentRepository repository for parent payment rows
     * @param installmentPaymentRepository repository for installment payment
     *                                     subtype rows
     * @param workOrderPaymentRepository repository for work-order payment
     *                                   subtype rows
     * @param payrollPaymentRepository repository for payroll payment subtype
     *                                 rows
     * @param bankAccountRepository repository used to resolve payment bank
     *                              accounts
     * @param personRepository repository used to resolve processors and staff
     * @param installmentRepository repository used to resolve installments
     * @param workOrderRepository repository used to resolve work orders
     * @param departmentRepository repository used to resolve payroll
     *                             departments
     * @param taskMonthlySalaryRepository repository used to validate payroll
     *                                    close rows before disbursement
     */
    public PaymentService(PaymentRepository paymentRepository,
                          InstallmentPaymentRepository installmentPaymentRepository,
                          WorkOrderPaymentRepository workOrderPaymentRepository,
                          PayrollPaymentRepository payrollPaymentRepository,
                          BankAccountRepository bankAccountRepository,
                          PersonRepository personRepository,
                          InstallmentRepository installmentRepository,
                          WorkOrderRepository workOrderRepository,
                          DepartmentRepository departmentRepository,
                          TaskMonthlySalaryRepository taskMonthlySalaryRepository,
                          CurrentUserService currentUserService) {
        this.paymentRepository = paymentRepository;
        this.installmentPaymentRepository = installmentPaymentRepository;
        this.workOrderPaymentRepository = workOrderPaymentRepository;
        this.payrollPaymentRepository = payrollPaymentRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.personRepository = personRepository;
        this.installmentRepository = installmentRepository;
        this.workOrderRepository = workOrderRepository;
        this.departmentRepository = departmentRepository;
        this.taskMonthlySalaryRepository = taskMonthlySalaryRepository;
        this.currentUserService = currentUserService;
    }

    // ── Installment Payment ─────────────────────────────────────────────

    /**
     * Creates an installment payment in one transaction.
     *
     * <p>Persists the parent {@link Payment} as an inbound tenant payment and
     * the child {@link InstallmentPayment} that links it to the installment
     * being paid. The parent money is validated and normalized before the
     * child record is saved.
     *
     * @param request the installment payment request, including parent payment
     *                data and the target installment
     * @return the created payment response
     * @throws ApiException if the referenced bank account, processor, or
     *                      installment does not exist, or if the payment
     *                      money or direction is invalid
     */
    @Transactional
    public PaymentResponse createInstallmentPayment(CreateInstallmentPaymentRequest request) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        Payment payment = createParentPayment(request.payment(), PaymentType.INSTALLMENT,
                PaymentDirection.INBOUND);

        Installment installment = installmentRepository.findById(request.installmentId())
                .orElseThrow(() -> new ApiException(LeasingErrorCode.INSTALLMENT_NOT_FOUND));

        ensureNoInstallmentPayment(payment.getId());
        updateInstallmentStatus(installment, payment);

        InstallmentPayment child = new InstallmentPayment();
        child.setPayment(payment);
        child.setInstallment(installment);
        child.setLateFee(request.lateFee());
        installmentPaymentRepository.save(child);

        log.info("Installment payment created: paymentNo={}, installmentId={}",
                payment.getPaymentNo(), request.installmentId());
        return toResponse(payment);
    }

    // ── Work Order Payment ──────────────────────────────────────────────

    /**
     * Creates a work order payment in one transaction.
     *
     * <p>Persists the parent {@link Payment} as an outbound vendor payment
     * and the child {@link WorkOrderPayment} that links it to the completed
     * work order. The work order must already be completed and is marked
     * paid after the child payment row is created.
     *
     * @param request the work-order payment request, including parent payment
     *                data and the target work order
     * @return the created payment response
     * @throws ApiException if the referenced bank account, processor, or
     *                      work order does not exist, or if the payment money
     *                      or direction is invalid
     */
    @Transactional
    public PaymentResponse createWorkOrderPayment(CreateWorkOrderPaymentRequest request) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        Payment payment = createParentPayment(request.payment(), PaymentType.WORK_ORDER,
                PaymentDirection.OUTBOUND);

        WorkOrder workOrder = workOrderRepository.findById(request.workOrderId())
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.WORK_ORDER_NOT_FOUND));

        ensureNoWorkOrderPayment(payment.getId());
        validateWorkOrderPayable(workOrder);
        workOrder.setJobStatus(WorkOrderStatus.PAID);
        workOrderRepository.save(workOrder);

        WorkOrderPayment child = new WorkOrderPayment();
        child.setPayment(payment);
        child.setWorkOrder(workOrder);
        child.setInvoiceNo(request.invoiceNo());
        workOrderPaymentRepository.save(child);

        log.info("Work order payment created: paymentNo={}, workOrderId={}",
                payment.getPaymentNo(), request.workOrderId());
        return toResponse(payment);
    }

    // ── Payroll Payment ─────────────────────────────────────────────────

    /**
     * Creates a payroll payment in one transaction.
     *
     * <p>Persists the parent {@link Payment} as an outbound staff payment and
     * the child {@link PayrollPayment} that links it to a staff member,
     * department, and payroll period. Payroll close calculations remain in
     * the workforce workflow; this method only records disbursement.
     *
     * @param request the payroll payment request, including parent payment
     *                data, staff, department, and period
     * @return the created payment response
     * @throws ApiException if the referenced bank account, processor, staff
     *                      member, or department does not exist, or if the
     *                      payment money or direction is invalid
     */
    @Transactional
    public PaymentResponse createPayrollPayment(CreatePayrollPaymentRequest request) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        Payment payment = createParentPayment(request.payment(), PaymentType.PAYROLL,
                PaymentDirection.OUTBOUND);

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        ensureNoPayrollPayment(payment.getId());
        TaskMonthlySalary salary = taskMonthlySalaryRepository
                .findByStaffIdAndDepartmentIdAndYearAndMonth(
                        request.staffId(), request.departmentId(),
                        request.payrollPeriod().year(), request.payrollPeriod().month())
                .orElseThrow(() -> new ApiException(FinanceErrorCode.PAYROLL_RECORD_NOT_FOUND));
        Money monthlyNetSalary = salary.getMonthlyNetSalary();
        if (monthlyNetSalary != null && payment.getMoney() != null) {
            if (!monthlyNetSalary.getCurrency().equals(payment.getMoney().getCurrency())) {
                throw new ApiException(FinanceErrorCode.MONEY_CURRENCY_MISMATCH);
            }
            if (monthlyNetSalary.getAmount().compareTo(payment.getMoney().getAmount()) != 0) {
                throw new ApiException(FinanceErrorCode.PAYMENT_AMOUNT_INVALID);
            }
        }

        PayrollPayment child = new PayrollPayment();
        child.setPayment(payment);
        child.setStaff(staff);
        child.setDepartment(department);
        child.setPayrollPeriod(request.payrollPeriod());
        payrollPaymentRepository.save(child);

        log.info("Payroll payment created: paymentNo={}, staffId={}, period={}-{}",
                payment.getPaymentNo(), request.staffId(),
                request.payrollPeriod().year(), request.payrollPeriod().month());
        return toResponse(payment);
    }

    // ── Query ───────────────────────────────────────────────────────────

    /**
     * Lists all payments with pagination.
     *
     * @param pageable the pagination and sorting request
     * @return a paged response of payment DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> findAll(Pageable pageable) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        return PagedResponse.from(paymentRepository.findAll(pageable), this::toResponse);
    }

    /**
     * Finds a single payment by ID.
     *
     * @param id the payment UUID primary key
     * @return the matching payment response
     * @throws ApiException if no payment exists with this ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ApiException(FinanceErrorCode.PAYMENT_NOT_FOUND));
        return toResponse(payment);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Payment createParentPayment(CreatePaymentRequest req, PaymentType enforceType,
                                        PaymentDirection expectedDirection) {
        PaymentDirection direction = rules.validateDirection(req.direction());
        if (direction != expectedDirection) {
            throw new ApiException(FinanceErrorCode.PAYMENT_DIRECTION_MISMATCH);
        }
        PaymentMethod method = rules.validateMethod(req.method());
        Money money = Money.positive(req.money().getAmount(), req.money().getCurrency());

        BankAccount bankAccount = bankAccountRepository.findById(req.bankAccountId())
                .orElseThrow(() -> new ApiException(FinanceErrorCode.BANK_ACCOUNT_NOT_FOUND));
        Person processedBy = req.processedById() != null
                ? personRepository.findById(req.processedById())
                        .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND))
                : null;

        Payment payment = Payment.builder()
                .paymentNo(req.paymentNo())
                .paidAt(Instant.now())
                .money(money)
                .paymentType(enforceType)
                .method(method)
                .direction(direction)
                .referenceNo(req.referenceNo())
                .bankAccount(bankAccount)
                .processedBy(processedBy)
                .build();
        return paymentRepository.save(payment);
    }


    private void ensureNoInstallmentPayment(UUID paymentId) {
        if (installmentPaymentRepository.existsByPaymentId(paymentId)) {
            throw new ApiException(FinanceErrorCode.PAYMENT_DETAIL_DUPLICATE);
        }
    }

    private void ensureNoWorkOrderPayment(UUID paymentId) {
        if (workOrderPaymentRepository.existsByPaymentId(paymentId)) {
            throw new ApiException(FinanceErrorCode.PAYMENT_DETAIL_DUPLICATE);
        }
    }

    private void ensureNoPayrollPayment(UUID paymentId) {
        if (payrollPaymentRepository.existsByPaymentId(paymentId)) {
            throw new ApiException(FinanceErrorCode.PAYMENT_DETAIL_DUPLICATE);
        }
    }

    private void updateInstallmentStatus(Installment installment, Payment payment) {
        if (installment.getInstallmentStatus() == InstallmentStatus.PAID) {
            throw new ApiException(LeasingErrorCode.INSTALLMENT_ALREADY_PAID);
        }
        InstallmentStatus nextStatus;
        if (installment.getAmountExpected() != null && payment.getMoney() != null
                && payment.getMoney().getAmount().compareTo(
                        installment.getAmountExpected().getAmount()) >= 0) {
            nextStatus = InstallmentStatus.PAID;
        } else {
            nextStatus = InstallmentStatus.PARTIALLY_PAID;
        }
        if (!installment.getInstallmentStatus().canTransitionTo(nextStatus)) {
            throw new ApiException(LeasingErrorCode.INSTALLMENT_STATUS_TRANSITION_INVALID);
        }
        installment.setInstallmentStatus(nextStatus);
        installmentRepository.save(installment);
    }

    private void validateWorkOrderPayable(WorkOrder workOrder) {
        if (workOrder.getJobStatus() != WorkOrderStatus.COMPLETED
                || !workOrder.getJobStatus().canTransitionTo(WorkOrderStatus.PAID)) {
            throw new ApiException(FinanceErrorCode.WORK_ORDER_NOT_PAYABLE);
        }
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getPaymentNo(), p.getPaidAt(),
                p.getMoney(), p.getPaymentType(),
                p.getMethod(), p.getDirection(), p.getReferenceNo(),
                p.getReconciliationStatus(),
                p.getBankAccount().getId(),
                p.getProcessedBy() != null ? p.getProcessedBy().getId() : null);
    }
}
