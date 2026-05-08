package com.cpmss.performance.kpipolicy;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.performance.kpipolicy.dto.CreateKpiPolicyRequest;
import com.cpmss.performance.kpipolicy.dto.KpiPolicyResponse;
import com.cpmss.performance.kpipolicy.dto.UpdateKpiPolicyRequest;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Orchestrates KPI policy tier operations.
 *
 * @see KpiPolicyRepository
 */
@Service
public class KpiPolicyService {

    private static final Logger log = LoggerFactory.getLogger(KpiPolicyService.class);

    private final KpiPolicyRepository repository;
    private final DepartmentRepository departmentRepository;
    private final PersonRepository personRepository;
    private final KpiPolicyMapper mapper;
    private final KpiPolicyRules rules = new KpiPolicyRules();

    public KpiPolicyService(KpiPolicyRepository repository,
                            DepartmentRepository departmentRepository,
                            PersonRepository personRepository,
                            KpiPolicyMapper mapper) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.personRepository = personRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public KpiPolicyResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<KpiPolicyResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    @Transactional
    public KpiPolicyResponse create(CreateKpiPolicyRequest request) {
        rules.validateScoreRange(request.minKpiScore(), request.maxKpiScore());

        Department dept = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));
        Person approver = personRepository.findById(request.approvedById())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.approvedById()));

        KpiPolicy policy = KpiPolicy.builder()
                .department(dept)
                .effectiveDate(request.effectiveDate())
                .tierLabel(request.tierLabel())
                .minKpiScore(request.minKpiScore())
                .maxKpiScore(request.maxKpiScore())
                .bonusRate(request.bonusRate() != null ? request.bonusRate() : BigDecimal.ZERO)
                .deductionRate(request.deductionRate() != null ? request.deductionRate() : BigDecimal.ZERO)
                .approvedBy(approver)
                .build();
        policy = repository.save(policy);
        log.info("KPI policy created: {} for dept {}", policy.getId(), request.departmentId());
        return mapper.toResponse(policy);
    }

    @Transactional
    public KpiPolicyResponse update(UUID id, UpdateKpiPolicyRequest request) {
        KpiPolicy policy = findOrThrow(id);

        rules.validateScoreRange(request.minKpiScore(), request.maxKpiScore());

        policy.setTierLabel(request.tierLabel());
        policy.setMinKpiScore(request.minKpiScore());
        policy.setMaxKpiScore(request.maxKpiScore());
        policy.setBonusRate(request.bonusRate() != null ? request.bonusRate() : BigDecimal.ZERO);
        policy.setDeductionRate(request.deductionRate() != null ? request.deductionRate() : BigDecimal.ZERO);
        policy = repository.save(policy);
        log.info("KPI policy updated: {}", policy.getId());
        return mapper.toResponse(policy);
    }

    private KpiPolicy findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KpiPolicy", id));
    }
}
