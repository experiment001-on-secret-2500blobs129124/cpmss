package com.cpmss.finance.bankaccount;

import com.cpmss.finance.bankaccount.dto.BankAccountResponse;
import com.cpmss.finance.bankaccount.dto.CreateBankAccountRequest;
import com.cpmss.finance.bankaccount.dto.UpdateBankAccountRequest;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for bank account CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, and update
 * endpoints under {@link ApiPaths#BANK_ACCOUNTS}. No delete endpoint —
 * bank accounts are permanent financial records.
 *
 * @see BankAccountService
 */
@RestController
public class BankAccountApiController {

    private final BankAccountService bankAccountService;

    /**
     * Constructs the controller with the bank account service.
     *
     * @param bankAccountService bank account orchestration service
     */
    public BankAccountApiController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    /**
     * Lists all bank accounts with pagination.
     *
     * @param pageable       pagination parameters (page, size, sort)
     * @param accountOwnerId optional person UUID for self-scoped account lookup
     * @return 200 OK with paginated bank account list
     */
    @GetMapping(ApiPaths.BANK_ACCOUNTS)
    public ResponseEntity<ApiResponse<PagedResponse<BankAccountResponse>>> listAll(
            Pageable pageable,
            @RequestParam(required = false) UUID accountOwnerId) {
        return ResponseEntity.ok(ApiResponse.ok(
                bankAccountService.listAll(pageable, accountOwnerId)));
    }

    /**
     * Retrieves a single bank account by ID.
     *
     * @param id the bank account UUID
     * @return 200 OK with the bank account
     */
    @GetMapping(ApiPaths.BANK_ACCOUNTS_BY_ID)
    public ResponseEntity<ApiResponse<BankAccountResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(bankAccountService.getById(id)));
    }

    /**
     * Creates a new bank account.
     *
     * @param request the bank account details and owner ID
     * @return 201 Created with the new bank account
     */
    @PostMapping(ApiPaths.BANK_ACCOUNTS)
    public ResponseEntity<ApiResponse<BankAccountResponse>> create(
            @Valid @RequestBody CreateBankAccountRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(bankAccountService.create(request)));
    }

    /**
     * Updates an existing bank account.
     *
     * @param id      the bank account UUID
     * @param request the updated bank account details
     * @return 200 OK with the updated bank account
     */
    @PutMapping(ApiPaths.BANK_ACCOUNTS_BY_ID)
    public ResponseEntity<ApiResponse<BankAccountResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBankAccountRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(bankAccountService.update(id, request)));
    }
}
