package com.cpmss.leasing.contract;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.leasing.contract.dto.ContractResponse;
import com.cpmss.leasing.contract.dto.CreateContractRequest;
import com.cpmss.leasing.contract.dto.UpdateContractRequest;
import com.cpmss.leasing.contractparty.dto.AddContractPartyRequest;
import com.cpmss.leasing.contractparty.dto.ContractPartyResponse;
import com.cpmss.leasing.personresidesunder.dto.AddPersonResidesUnderRequest;
import com.cpmss.leasing.personresidesunder.dto.PersonResidesUnderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for contract CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, and update
 * endpoints under {@link ApiPaths#CONTRACTS}. No delete endpoint —
 * contracts are permanent records closed by status change.
 *
 * @see ContractService
 */
@RestController
public class ContractApiController {

    private final ContractService contractService;

    /**
     * Constructs the controller with the contract service.
     *
     * @param contractService contract orchestration service
     */
    public ContractApiController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * Lists all contracts with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated contract list
     */
    @GetMapping(ApiPaths.CONTRACTS)
    public ResponseEntity<ApiResponse<PagedResponse<ContractResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.listAll(pageable)));
    }

    /**
     * Retrieves a single contract by ID.
     *
     * @param id the contract UUID
     * @return 200 OK with the contract
     */
    @GetMapping(ApiPaths.CONTRACTS_BY_ID)
    public ResponseEntity<ApiResponse<ContractResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.getById(id)));
    }

    /**
     * Creates a new contract.
     *
     * @param request the contract details and target ID
     * @return 201 Created with the new contract
     */
    @PostMapping(ApiPaths.CONTRACTS)
    public ResponseEntity<ApiResponse<ContractResponse>> create(
            @Valid @RequestBody CreateContractRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(contractService.create(request)));
    }

    /**
     * Updates an existing contract.
     *
     * @param id      the contract UUID
     * @param request the updated contract details
     * @return 200 OK with the updated contract
     */
    @PutMapping(ApiPaths.CONTRACTS_BY_ID)
    public ResponseEntity<ApiResponse<ContractResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateContractRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.update(id, request)));
    }

    // ── Contract Party Sub-Endpoints ────────────────────────────────────

    /**
     * Adds a party to a contract.
     *
     * @param id      the contract UUID
     * @param request the party details
     * @return 201 Created with the new contract party
     */
    @PostMapping(ApiPaths.CONTRACT_PARTIES)
    public ResponseEntity<ApiResponse<ContractPartyResponse>> addParty(
            @PathVariable UUID id,
            @Valid @RequestBody AddContractPartyRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(contractService.addParty(id, request)));
    }

    /**
     * Lists all parties for a contract.
     *
     * @param id the contract UUID
     * @return 200 OK with the party list
     */
    @GetMapping(ApiPaths.CONTRACT_PARTIES)
    public ResponseEntity<ApiResponse<java.util.List<ContractPartyResponse>>> getParties(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.getParties(id)));
    }

    // ── Resident Sub-Endpoints ──────────────────────────────────────────

    /**
     * Adds a resident under a contract.
     *
     * @param id      the contract UUID
     * @param request the resident details
     * @return 201 Created with the new residency record
     */
    @PostMapping(ApiPaths.CONTRACT_RESIDENTS)
    public ResponseEntity<ApiResponse<PersonResidesUnderResponse>> addResident(
            @PathVariable UUID id,
            @Valid @RequestBody AddPersonResidesUnderRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(contractService.addResident(id, request)));
    }

    /**
     * Lists all residents for a contract.
     *
     * @param id the contract UUID
     * @return 200 OK with the resident list
     */
    @GetMapping(ApiPaths.CONTRACT_RESIDENTS)
    public ResponseEntity<ApiResponse<java.util.List<PersonResidesUnderResponse>>> getResidents(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.getResidents(id)));
    }
}
