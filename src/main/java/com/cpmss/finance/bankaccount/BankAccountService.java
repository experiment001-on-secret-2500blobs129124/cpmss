package com.cpmss.finance.bankaccount;

import com.cpmss.finance.bankaccount.dto.BankAccountResponse;
import com.cpmss.finance.bankaccount.dto.CreateBankAccountRequest;
import com.cpmss.finance.bankaccount.dto.UpdateBankAccountRequest;
import com.cpmss.finance.common.FinanceAccessRules;
import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.property.compound.Compound;
import com.cpmss.property.compound.CompoundRepository;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.common.PropertyErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates bank account lifecycle operations.
 *
 * <p>Bank accounts are owned by exactly one of: a {@link Compound},
 * a {@link Person}, or a {@link Company}. The mutual exclusion
 * constraint is enforced by {@link BankAccountRules}.
 *
 * <p>Bank accounts are permanent financial records — never deleted.
 *
 * @see BankAccountRules
 * @see BankAccountRepository
 */
@Service
public class BankAccountService {

    private static final Logger log = LoggerFactory.getLogger(BankAccountService.class);

    private final BankAccountRepository repository;
    private final CompoundRepository compoundRepository;
    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;
    private final BankAccountMapper mapper;
    private final CurrentUserService currentUserService;
    private final BankAccountRules rules = new BankAccountRules();
    private final FinanceAccessRules accessRules = new FinanceAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         bank account data access
     * @param compoundRepository compound data access (owner FK lookup)
     * @param personRepository   person data access (owner FK lookup)
     * @param companyRepository  company data access (owner FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public BankAccountService(BankAccountRepository repository,
                              CompoundRepository compoundRepository,
                              PersonRepository personRepository,
                              CompanyRepository companyRepository,
                              BankAccountMapper mapper,
                              CurrentUserService currentUserService) {
        this.repository = repository;
        this.compoundRepository = compoundRepository;
        this.personRepository = personRepository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a bank account by its unique identifier.
     *
     * @param id the bank account's UUID primary key
     * @return the matching bank account response
     * @throws ApiException if no bank account exists with this ID
     */
    @Transactional(readOnly = true)
    public BankAccountResponse getById(UUID id) {
        BankAccount account = repository.findById(id)
                .orElseThrow(() -> new ApiException(FinanceErrorCode.BANK_ACCOUNT_NOT_FOUND));
        accessRules.requireCanViewBankAccount(currentUserService.currentUser(), account);
        return mapper.toResponse(account);
    }

    /**
     * Lists all bank accounts with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of bank account DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<BankAccountResponse> listAll(Pageable pageable) {
        return listAll(pageable, null);
    }

    @Transactional(readOnly = true)
    public PagedResponse<BankAccountResponse> listAll(Pageable pageable, UUID accountOwnerId) {
        accessRules.requireCanListBankAccounts(currentUserService.currentUser(), accountOwnerId);
        if (accountOwnerId != null) {
            return PagedResponse.from(
                    repository.findByAccountOwnerId(accountOwnerId, pageable), mapper::toResponse);
        }
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new bank account with exactly one owner.
     *
     * @param request the create request with bank details and owner ID
     * @return the created bank account response
     * @throws ApiException if the owner rule is violated
     */
    @Transactional
    public BankAccountResponse create(CreateBankAccountRequest request) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        rules.validateExactlyOneOwner(
                request.compoundId(), request.accountOwnerId(), request.companyId());

        BankAccount account = BankAccount.builder()
                .bankName(request.bankName())
                .iban(request.iban())
                .swiftCode(request.swiftCode())
                .isPrimary(request.isPrimary() != null ? request.isPrimary() : false)
                .compound(resolveCompound(request.compoundId()))
                .accountOwner(resolvePerson(request.accountOwnerId()))
                .company(resolveCompany(request.companyId()))
                .build();
        account = repository.save(account);
        log.info("Bank account created: {} at {}", account.getId(), account.getBankName());
        return mapper.toResponse(account);
    }

    /**
     * Updates an existing bank account.
     *
     * @param id      the bank account's UUID
     * @param request the update request with new values
     * @return the updated bank account response
     * @throws ApiException if no bank account exists with this ID
     */
    @Transactional
    public BankAccountResponse update(UUID id, UpdateBankAccountRequest request) {
        accessRules.requireFinanceAuthority(currentUserService.currentUser());
        BankAccount account = repository.findById(id)
                .orElseThrow(() -> new ApiException(FinanceErrorCode.BANK_ACCOUNT_NOT_FOUND));

        rules.validateExactlyOneOwner(
                request.compoundId(), request.accountOwnerId(), request.companyId());

        account.setBankName(request.bankName());
        account.setIban(request.iban());
        account.setSwiftCode(request.swiftCode());
        account.setIsPrimary(request.isPrimary() != null ? request.isPrimary() : false);
        account.setCompound(resolveCompound(request.compoundId()));
        account.setAccountOwner(resolvePerson(request.accountOwnerId()));
        account.setCompany(resolveCompany(request.companyId()));
        account = repository.save(account);
        log.info("Bank account updated: {}", account.getId());
        return mapper.toResponse(account);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Compound resolveCompound(UUID id) {
        if (id == null) {
            return null;
        }
        return compoundRepository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.COMPOUND_NOT_FOUND));
    }

    private Person resolvePerson(UUID id) {
        if (id == null) {
            return null;
        }
        return personRepository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
    }

    private Company resolveCompany(UUID id) {
        if (id == null) {
            return null;
        }
        return companyRepository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.COMPANY_NOT_FOUND));
    }
}
