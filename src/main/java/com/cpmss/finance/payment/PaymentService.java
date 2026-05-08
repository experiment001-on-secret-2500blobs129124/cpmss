package com.cpmss.finance.payment;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.finance.bankaccount.BankAccountRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.leasing.installment.Installment;
import com.cpmss.leasing.installment.InstallmentRepository;
import com.cpmss.finance.installmentpayment.InstallmentPayment;
import com.cpmss.finance.installmentpayment.InstallmentPaymentRepository;
import com.cpmss.finance.installmentpayment.dto.CreateInstallmentPaymentRequest;
import com.cpmss.finance.money.Money;
import com.cpmss.finance.payment.dto.CreatePaymentRequest;
import com.cpmss.finance.payment.dto.PaymentResponse;
import com.cpmss.finance.payrollpayment.PayrollPayment;
import com.cpmss.finance.payrollpayment.PayrollPaymentRepository;
import com.cpmss.finance.payrollpayment.dto.CreatePayrollPaymentRequest;
import com.cpmss.platform.exception.BusinessException;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.maintenance.workorder.WorkOrder;
import com.cpmss.maintenance.workorder.WorkOrderRepository;
import com.cpmss.finance.workorderpayment.WorkOrderPayment;
import com.cpmss.finance.workorderpayment.WorkOrderPaymentRepository;
import com.cpmss.finance.workorderpayment.dto.CreateWorkOrderPaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Manages polymorphic payment creation and querying.
 *
 * <p>Each create method is @Transactional, creating a parent Payment
 * record and exactly one child record (InstallmentPayment,
 * WorkOrderPayment, or PayrollPayment). Parent payment amount and currency
 * are normalized through {@link Money} before the row is persisted, while
 * request and response DTOs keep their existing primitive shape.
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
    private final PaymentRules rules = new PaymentRules();

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
     */
    public PaymentService(PaymentRepository paymentRepository,
                          InstallmentPaymentRepository installmentPaymentRepository,
                          WorkOrderPaymentRepository workOrderPaymentRepository,
                          PayrollPaymentRepository payrollPaymentRepository,
                          BankAccountRepository bankAccountRepository,
                          PersonRepository personRepository,
                          InstallmentRepository installmentRepository,
                          WorkOrderRepository workOrderRepository,
                          DepartmentRepository departmentRepository) {
        this.paymentRepository = paymentRepository;
        this.installmentPaymentRepository = installmentPaymentRepository;
        this.workOrderPaymentRepository = workOrderPaymentRepository;
        this.payrollPaymentRepository = payrollPaymentRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.personRepository = personRepository;
        this.installmentRepository = installmentRepository;
        this.workOrderRepository = workOrderRepository;
        this.departmentRepository = departmentRepository;
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
     * @throws ResourceNotFoundException if the referenced bank account,
     *                                   processor, or installment does not
     *                                   exist
     * @throws BusinessException if the payment money or direction is invalid
     */
    @Transactional
    public PaymentResponse createInstallmentPayment(CreateInstallmentPaymentRequest request) {
        Payment payment = createParentPayment(request.payment(), "Installment");

        Installment installment = installmentRepository.findById(request.installmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Installment", request.installmentId()));

        InstallmentPayment child = new InstallmentPayment();
        child.setPayment(payment);
        child.setInstallment(installment);
        child.setLateFeeAmount(request.lateFeeAmount());
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
     * work order. This method does not perform work-order lifecycle changes;
     * it only records the financial movement.
     *
     * @param request the work-order payment request, including parent payment
     *                data and the target work order
     * @return the created payment response
     * @throws ResourceNotFoundException if the referenced bank account,
     *                                   processor, or work order does not
     *                                   exist
     * @throws BusinessException if the payment money or direction is invalid
     */
    @Transactional
    public PaymentResponse createWorkOrderPayment(CreateWorkOrderPaymentRequest request) {
        Payment payment = createParentPayment(request.payment(), "WorkOrder");

        WorkOrder workOrder = workOrderRepository.findById(request.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", request.workOrderId()));

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
     * @throws ResourceNotFoundException if the referenced bank account,
     *                                   processor, staff member, or department
     *                                   does not exist
     * @throws BusinessException if the payment money or direction is invalid
     */
    @Transactional
    public PaymentResponse createPayrollPayment(CreatePayrollPaymentRequest request) {
        Payment payment = createParentPayment(request.payment(), "Payroll");

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        PayrollPayment child = new PayrollPayment();
        child.setPayment(payment);
        child.setStaff(staff);
        child.setDepartment(department);
        child.setYear(request.year());
        child.setMonth(request.month());
        payrollPaymentRepository.save(child);

        log.info("Payroll payment created: paymentNo={}, staffId={}, period={}-{}",
                payment.getPaymentNo(), request.staffId(), request.year(), request.month());
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
        return PagedResponse.from(paymentRepository.findAll(pageable), this::toResponse);
    }

    /**
     * Finds a single payment by ID.
     *
     * @param id the payment UUID primary key
     * @return the matching payment response
     * @throws ResourceNotFoundException if no payment exists with this ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return toResponse(payment);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Payment createParentPayment(CreatePaymentRequest req, String enforceType) {
        rules.validateDirection(req.direction());
        Money money = Money.positiveOrDefaultCurrency(req.amount(), req.currency());

        BankAccount bankAccount = bankAccountRepository.findById(req.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", req.bankAccountId()));
        Person processedBy = req.processedById() != null
                ? personRepository.findById(req.processedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Person", req.processedById()))
                : null;

        Payment payment = Payment.builder()
                .paymentNo(req.paymentNo())
                .paidAt(Instant.now())
                .money(money)
                .paymentType(enforceType)
                .method(req.method())
                .direction(req.direction())
                .referenceNo(req.referenceNo())
                .bankAccount(bankAccount)
                .processedBy(processedBy)
                .build();
        return paymentRepository.save(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getPaymentNo(), p.getPaidAt(),
                p.getAmount(), p.getCurrency(), p.getPaymentType(),
                p.getMethod(), p.getDirection(), p.getReferenceNo(),
                p.getReconciliationStatus(),
                p.getBankAccount().getId(),
                p.getProcessedBy() != null ? p.getProcessedBy().getId() : null);
    }
}
