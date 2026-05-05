package com.cpmss.payment;

import com.cpmss.bankaccount.BankAccount;
import com.cpmss.bankaccount.BankAccountRepository;
import com.cpmss.common.PagedResponse;
import com.cpmss.department.Department;
import com.cpmss.department.DepartmentRepository;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.installment.Installment;
import com.cpmss.installment.InstallmentRepository;
import com.cpmss.installmentpayment.InstallmentPayment;
import com.cpmss.installmentpayment.InstallmentPaymentRepository;
import com.cpmss.installmentpayment.dto.CreateInstallmentPaymentRequest;
import com.cpmss.payment.dto.CreatePaymentRequest;
import com.cpmss.payment.dto.PaymentResponse;
import com.cpmss.payrollpayment.PayrollPayment;
import com.cpmss.payrollpayment.PayrollPaymentRepository;
import com.cpmss.payrollpayment.dto.CreatePayrollPaymentRequest;
import com.cpmss.person.Person;
import com.cpmss.person.PersonRepository;
import com.cpmss.workorder.WorkOrder;
import com.cpmss.workorder.WorkOrderRepository;
import com.cpmss.workorderpayment.WorkOrderPayment;
import com.cpmss.workorderpayment.WorkOrderPaymentRepository;
import com.cpmss.workorderpayment.dto.CreateWorkOrderPaymentRequest;
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
 * WorkOrderPayment, or PayrollPayment).
 *
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
     * Creates an installment payment — Payment + InstallmentPayment in one transaction.
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
     * Creates a work order payment — Payment + WorkOrderPayment in one transaction.
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
     * Creates a payroll payment — Payment + PayrollPayment in one transaction.
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
     */
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> findAll(Pageable pageable) {
        return PagedResponse.from(paymentRepository.findAll(pageable), this::toResponse);
    }

    /**
     * Finds a single payment by ID.
     */
    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return toResponse(payment);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Payment createParentPayment(CreatePaymentRequest req, String enforceType) {
        rules.validateAmountPositive(req.amount());
        rules.validateDirection(req.direction());

        BankAccount bankAccount = bankAccountRepository.findById(req.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", req.bankAccountId()));
        Person processedBy = req.processedById() != null
                ? personRepository.findById(req.processedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Person", req.processedById()))
                : null;

        Payment payment = Payment.builder()
                .paymentNo(req.paymentNo())
                .paidAt(Instant.now())
                .amount(req.amount())
                .currency(req.currency() != null ? req.currency() : "USD")
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
