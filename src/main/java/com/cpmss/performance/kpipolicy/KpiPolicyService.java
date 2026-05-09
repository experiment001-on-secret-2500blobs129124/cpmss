package com.cpmss.performance.kpipolicy;

import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.performance.common.KpiScoreRange;
import com.cpmss.performance.common.PerformanceAccessRules;
import com.cpmss.performance.common.PercentageRate;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.kpipolicy.dto.CreateKpiPolicyRequest;
import com.cpmss.performance.kpipolicy.dto.KpiPolicyResponse;
import com.cpmss.performance.kpipolicy.dto.UpdateKpiPolicyRequest;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates KPI policy tier operations.
 *
 * <p>KPI policy fields are exposed as primitive DTO values, but the service
 * converts them into {@link PerformanceRating}, {@link KpiScoreRange}, and
 * {@link PercentageRate} before persisting the entity. This keeps the current
 * API contract stable while enforcing the same vocabulary and range rules as
 * the Flyway constraints.
 *
 * @see KpiPolicyRepository
 * @see KpiPolicyRules
 */
@Service
public class KpiPolicyService {

    private static final Logger log = LoggerFactory.getLogger(KpiPolicyService.class);

    private final KpiPolicyRepository repository;
    private final DepartmentRepository departmentRepository;
    private final PersonRepository personRepository;
    private final KpiPolicyMapper mapper;
    private final CurrentUserService currentUserService;
    private final KpiPolicyRules rules = new KpiPolicyRules();
    private final PerformanceAccessRules accessRules = new PerformanceAccessRules();

    /**
     * Creates the KPI policy service.
     *
     * @param repository repository for KPI policy rows
     * @param departmentRepository repository used to resolve owning departments
     * @param personRepository repository used to resolve approving managers
     * @param mapper mapper used to expose primitive DTO values
     */
    public KpiPolicyService(KpiPolicyRepository repository,
                            DepartmentRepository departmentRepository,
                            PersonRepository personRepository,
                            KpiPolicyMapper mapper,
                            CurrentUserService currentUserService) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.personRepository = personRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a KPI policy by ID.
     *
     * @param id the KPI policy UUID
     * @return the matching KPI policy response
    * @throws ApiException if no KPI policy exists with this ID
     */
    @Transactional(readOnly = true)
    public KpiPolicyResponse getById(UUID id) {
        accessRules.requireHrOrBusinessAdmin(currentUserService.currentUser());
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists KPI policies with pagination.
     *
     * @param pageable the pagination and sorting request
     * @return a paged response of KPI policies
     */
    @Transactional(readOnly = true)
    public PagedResponse<KpiPolicyResponse> listAll(Pageable pageable) {
        accessRules.requireHrOrBusinessAdmin(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a KPI policy tier.
     *
     * @param request the KPI policy creation request
     * @return the created KPI policy response
    * @throws ApiException if the department or approver does not exist, or the
    *                      tier label, score range, or rates are invalid
     */
    @Transactional
    public KpiPolicyResponse create(CreateKpiPolicyRequest request) {
        accessRules.requireHrOrBusinessAdmin(currentUserService.currentUser());
        KpiScoreRange scoreRange = rules.validateScoreRange(request.minKpiScore(), request.maxKpiScore());
        PerformanceRating tier = PerformanceRating.fromLabel(request.tierLabel());
        PercentageRate bonusRate = PercentageRate.orZero(request.bonusRate());
        PercentageRate deductionRate = PercentageRate.orZero(request.deductionRate());

        Department dept = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        Person approver = personRepository.findById(request.approvedById())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));

        KpiPolicy policy = KpiPolicy.builder()
                .department(dept)
                .effectiveDate(request.effectiveDate())
                .tierLabel(tier)
                .minKpiScore(scoreRange.min())
                .maxKpiScore(scoreRange.max())
                .bonusRate(bonusRate)
                .deductionRate(deductionRate)
                .approvedBy(approver)
                .build();
        policy = repository.save(policy);
        log.info("KPI policy created: {} for dept {}", policy.getId(), request.departmentId());
        return mapper.toResponse(policy);
    }

    /**
     * Updates a KPI policy tier.
     *
     * @param id the KPI policy UUID
     * @param request the replacement KPI policy values
     * @return the updated KPI policy response
    * @throws ApiException if no KPI policy exists with this ID or the tier
    *                      label, score range, or rates are invalid
     */
    @Transactional
    public KpiPolicyResponse update(UUID id, UpdateKpiPolicyRequest request) {
        accessRules.requireHrOrBusinessAdmin(currentUserService.currentUser());
        KpiPolicy policy = findOrThrow(id);

        KpiScoreRange scoreRange = rules.validateScoreRange(request.minKpiScore(), request.maxKpiScore());

        policy.setTierLabel(PerformanceRating.fromLabel(request.tierLabel()));
        policy.setMinKpiScore(scoreRange.min());
        policy.setMaxKpiScore(scoreRange.max());
        policy.setBonusRate(PercentageRate.orZero(request.bonusRate()));
        policy.setDeductionRate(PercentageRate.orZero(request.deductionRate()));
        policy = repository.save(policy);
        log.info("KPI policy updated: {}", policy.getId());
        return mapper.toResponse(policy);
    }

    private KpiPolicy findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(PerformanceErrorCode.KPI_POLICY_NOT_FOUND));
    }
}
