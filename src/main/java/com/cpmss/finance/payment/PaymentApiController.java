package com.cpmss.finance.payment;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.finance.installmentpayment.dto.CreateInstallmentPaymentRequest;
import com.cpmss.finance.payment.dto.PaymentResponse;
import com.cpmss.finance.payrollpayment.dto.CreatePayrollPaymentRequest;
import com.cpmss.finance.workorderpayment.dto.CreateWorkOrderPaymentRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for polymorphic payment operations.
 *
 * @see PaymentService
 */
@RestController
public class PaymentApiController {

    private final PaymentService paymentService;

    public PaymentApiController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Creates an installment payment. */
    @PostMapping(ApiPaths.PAYMENTS_INSTALLMENT)
    public ResponseEntity<ApiResponse<PaymentResponse>> createInstallmentPayment(
            @Valid @RequestBody CreateInstallmentPaymentRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(paymentService.createInstallmentPayment(request)));
    }

    /** Creates a work order payment. */
    @PostMapping(ApiPaths.PAYMENTS_WORK_ORDER)
    public ResponseEntity<ApiResponse<PaymentResponse>> createWorkOrderPayment(
            @Valid @RequestBody CreateWorkOrderPaymentRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(paymentService.createWorkOrderPayment(request)));
    }

    /** Creates a payroll payment. */
    @PostMapping(ApiPaths.PAYMENTS_PAYROLL)
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayrollPayment(
            @Valid @RequestBody CreatePayrollPaymentRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(paymentService.createPayrollPayment(request)));
    }

    /** Lists all payments with pagination. */
    @GetMapping(ApiPaths.PAYMENTS)
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> listPayments(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.findAll(pageable)));
    }

    /** Finds a single payment by ID. */
    @GetMapping(ApiPaths.PAYMENTS_BY_ID)
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.findById(id)));
    }
}
