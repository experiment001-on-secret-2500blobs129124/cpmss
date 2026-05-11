package com.cpmss.hr.recruitment;

import com.cpmss.hr.application.dto.ApplicationResponse;
import com.cpmss.hr.application.dto.CreateApplicationRequest;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.hr.hireagreement.dto.CreateHireAgreementRequest;
import com.cpmss.hr.hireagreement.dto.HireAgreementResponse;
import com.cpmss.hr.recruitment.dto.CreateRecruitmentRequest;
import com.cpmss.hr.recruitment.dto.RecruitmentResponse;
import com.cpmss.hr.recruitment.dto.UpdateRecruitmentRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the hiring pipeline workflow (US-1).
 *
 * <p>Exposes endpoints for application submission, interview scheduling,
 * result recording, and hire agreement creation. Composite-key entities
 * use flat paths with key parts passed in request bodies.
 *
 * @see RecruitmentService
 */
@RestController
public class RecruitmentApiController {

    private final RecruitmentService recruitmentService;

    public RecruitmentApiController(RecruitmentService recruitmentService) {
        this.recruitmentService = recruitmentService;
    }

    /**
     * Submits a new job application.
     *
     * @param request the application details
     * @return 201 Created with the new application
     */
    @PostMapping(ApiPaths.APPLICATIONS)
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplication(
            @Valid @RequestBody CreateApplicationRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(recruitmentService.submitApplication(request)));
    }

    /**
     * Lists applications owned by the current applicant.
     *
     * @return 200 OK with the applicant's applications
     */
    @GetMapping(ApiPaths.APPLICATIONS_MINE)
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> listMyApplications() {
        return ResponseEntity.ok(ApiResponse.ok(recruitmentService.listMyApplications()));
    }

    /**
     * Lists all applications with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with the application list
     */
    @GetMapping(ApiPaths.APPLICATIONS)
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> listApplications(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(recruitmentService.listApplications(pageable)));
    }

    /**
     * Lists all interview records for HR review.
     *
     * @param pageable pagination parameters
     * @return 200 OK with the interview list
     */
    @GetMapping(ApiPaths.INTERVIEWS)
    public ResponseEntity<ApiResponse<List<RecruitmentResponse>>> listInterviews(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(recruitmentService.listInterviews(pageable)));
    }

    /**
     * Lists interview schedule/history for the current applicant.
     *
     * @return 200 OK with the applicant's interview records
     */
    @GetMapping(ApiPaths.INTERVIEWS_MINE)
    public ResponseEntity<ApiResponse<List<RecruitmentResponse>>> listMyInterviews() {
        return ResponseEntity.ok(ApiResponse.ok(recruitmentService.listMyInterviews()));
    }

    /**
     * Schedules an interview for an application.
     *
     * @param request the interview details (includes application composite key)
     * @return 201 Created with the new interview record
     */
    @PostMapping(ApiPaths.INTERVIEWS)
    public ResponseEntity<ApiResponse<RecruitmentResponse>> scheduleInterview(
            @Valid @RequestBody CreateRecruitmentRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(recruitmentService.scheduleInterview(request)));
    }

    /**
     * Records the result of an interview.
     *
     * @param request the result details (includes interview composite key)
     * @return 200 OK with the updated interview record
     */
    @PutMapping(ApiPaths.INTERVIEWS_RESULT)
    public ResponseEntity<ApiResponse<RecruitmentResponse>> recordResult(
            @Valid @RequestBody UpdateRecruitmentRequest request) {
        RecruitmentId id = new RecruitmentId(
                request.interviewerId(),
                request.applicantId(),
                request.positionId(),
                request.applicationDate(),
                request.interviewDate());
        return ResponseEntity.ok(ApiResponse.ok(recruitmentService.recordResult(id, request)));
    }

    /**
     * Lists all hire agreements for HR review.
     *
     * @param pageable pagination parameters
     * @return 200 OK with the hire agreement list
     */
    @GetMapping(ApiPaths.HIRE_AGREEMENTS)
    public ResponseEntity<ApiResponse<List<HireAgreementResponse>>> listHireAgreements(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(recruitmentService.listHireAgreements(pageable)));
    }

    /**
     * Creates a hire agreement and onboards the applicant.
     *
     * @param request the hire agreement details
     * @return 201 Created with the new hire agreement
     */
    @PostMapping(ApiPaths.HIRE_AGREEMENTS)
    public ResponseEntity<ApiResponse<HireAgreementResponse>> createHireAgreement(
            @Valid @RequestBody CreateHireAgreementRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(recruitmentService.createHireAgreement(request)));
    }
}
