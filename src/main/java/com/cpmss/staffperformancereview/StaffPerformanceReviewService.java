package com.cpmss.staffperformancereview;

import com.cpmss.common.PagedResponse;
import com.cpmss.department.Department;
import com.cpmss.department.DepartmentRepository;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.person.Person;
import com.cpmss.person.PersonRepository;
import com.cpmss.staffperformancereview.dto.CreateStaffPerformanceReviewRequest;
import com.cpmss.staffperformancereview.dto.StaffPerformanceReviewResponse;
import com.cpmss.staffperformancereview.dto.UpdateStaffPerformanceReviewRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Orchestrates staff performance review operations. */
@Service
public class StaffPerformanceReviewService {

    private static final Logger log = LoggerFactory.getLogger(StaffPerformanceReviewService.class);

    private final StaffPerformanceReviewRepository repository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffPerformanceReviewMapper mapper;

    public StaffPerformanceReviewService(StaffPerformanceReviewRepository repository,
                                         PersonRepository personRepository,
                                         DepartmentRepository departmentRepository,
                                         StaffPerformanceReviewMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public StaffPerformanceReviewResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<StaffPerformanceReviewResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    @Transactional
    public StaffPerformanceReviewResponse create(CreateStaffPerformanceReviewRequest request) {
        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        Person reviewer = personRepository.findById(request.reviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.reviewerId()));
        Department dept = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        StaffPerformanceReview review = StaffPerformanceReview.builder()
                .staff(staff)
                .reviewer(reviewer)
                .department(dept)
                .reviewDate(request.reviewDate())
                .overallKpiScore(request.overallKpiScore())
                .overallRating(request.overallRating())
                .notes(request.notes())
                .resultedInPromotion(request.resultedInPromotion() != null ? request.resultedInPromotion() : false)
                .resultedInRaise(request.resultedInRaise() != null ? request.resultedInRaise() : false)
                .build();
        review = repository.save(review);
        log.info("Performance review created: {} for staff {}", review.getId(), request.staffId());
        return mapper.toResponse(review);
    }

    @Transactional
    public StaffPerformanceReviewResponse update(UUID id, UpdateStaffPerformanceReviewRequest request) {
        StaffPerformanceReview review = findOrThrow(id);
        review.setOverallKpiScore(request.overallKpiScore());
        review.setOverallRating(request.overallRating());
        review.setNotes(request.notes());
        review.setResultedInPromotion(request.resultedInPromotion() != null ? request.resultedInPromotion() : false);
        review.setResultedInRaise(request.resultedInRaise() != null ? request.resultedInRaise() : false);
        review = repository.save(review);
        log.info("Performance review updated: {}", review.getId());
        return mapper.toResponse(review);
    }

    private StaffPerformanceReview findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPerformanceReview", id));
    }
}
