package com.cpmss.hr.recruitment;

import com.cpmss.hr.application.Application;
import com.cpmss.hr.application.ApplicationId;
import com.cpmss.hr.application.ApplicationRepository;
import com.cpmss.hr.application.dto.ApplicationResponse;
import com.cpmss.hr.application.dto.CreateApplicationRequest;
import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.hr.hireagreement.HireAgreement;
import com.cpmss.hr.hireagreement.HireAgreementRepository;
import com.cpmss.hr.hireagreement.dto.CreateHireAgreementRequest;
import com.cpmss.hr.hireagreement.dto.HireAgreementResponse;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.people.qualification.Qualification;
import com.cpmss.people.qualification.QualificationRepository;
import com.cpmss.hr.recruitment.dto.CreateRecruitmentRequest;
import com.cpmss.hr.recruitment.dto.RecruitmentResponse;
import com.cpmss.hr.recruitment.dto.UpdateRecruitmentRequest;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.hr.staffposition.StaffPositionRepository;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistory;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffprofile.StaffProfile;
import com.cpmss.hr.staffprofile.StaffProfileRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistory;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Orchestrates the hiring pipeline workflow (US-1).
 *
 * <p>Manages the full lifecycle: application submission → interview
 * scheduling → result recording → hire agreement creation with
 * transactional onboarding (StaffProfile + StaffPositionHistory +
 * StaffSalaryHistory).
 *
 * @see HireAgreementRules
 * @see Application
 * @see Recruitment
 * @see HireAgreement
 */
@Service
public class RecruitmentService {

    private static final Logger log = LoggerFactory.getLogger(RecruitmentService.class);

    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final HireAgreementRepository hireAgreementRepository;
    private final PersonRepository personRepository;
    private final StaffPositionRepository staffPositionRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final StaffPositionHistoryRepository staffPositionHistoryRepository;
    private final StaffSalaryHistoryRepository staffSalaryHistoryRepository;
    private final QualificationRepository qualificationRepository;
    private final CurrentUserService currentUserService;
    private final HireAgreementRules hireAgreementRules = new HireAgreementRules();
    private final HrAccessRules accessRules = new HrAccessRules();

    /**
     * Constructs the service with required dependencies.
     */
    public RecruitmentService(ApplicationRepository applicationRepository,
                              RecruitmentRepository recruitmentRepository,
                              HireAgreementRepository hireAgreementRepository,
                              PersonRepository personRepository,
                              StaffPositionRepository staffPositionRepository,
                              StaffProfileRepository staffProfileRepository,
                              StaffPositionHistoryRepository staffPositionHistoryRepository,
                              StaffSalaryHistoryRepository staffSalaryHistoryRepository,
                              QualificationRepository qualificationRepository,
                              CurrentUserService currentUserService) {
        this.applicationRepository = applicationRepository;
        this.recruitmentRepository = recruitmentRepository;
        this.hireAgreementRepository = hireAgreementRepository;
        this.personRepository = personRepository;
        this.staffPositionRepository = staffPositionRepository;
        this.staffProfileRepository = staffProfileRepository;
        this.staffPositionHistoryRepository = staffPositionHistoryRepository;
        this.staffSalaryHistoryRepository = staffSalaryHistoryRepository;
        this.qualificationRepository = qualificationRepository;
        this.currentUserService = currentUserService;
    }

    // ── Application Operations ──────────────────────────────────────────

    /**
     * Submits a new job application.
     *
     * @param request the application details
     * @return the created application response
     * @throws ApiException if applicant or position not found
     */
    @Transactional
    public ApplicationResponse submitApplication(CreateApplicationRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        Person applicant = findPersonOrThrow(request.applicantId());
        StaffPosition position = findPositionOrThrow(request.positionId());

        Application application = new Application();
        application.setApplicant(applicant);
        application.setPosition(position);
        application.setApplicationDate(request.applicationDate());
        application = applicationRepository.save(application);
        log.info("Application submitted: applicant={}, position={}, date={}",
                request.applicantId(), request.positionId(), request.applicationDate());
        return toApplicationResponse(application);
    }

    /**
     * Lists all applications with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of application responses
     */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> listApplications(Pageable pageable) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        return applicationRepository.findAll(pageable)
                .map(this::toApplicationResponse)
                .getContent();
    }

    // ── Interview Operations ────────────────────────────────────────────

    /**
     * Schedules an interview for an application.
     *
     * @param request the interview details including application composite key
     * @return the created recruitment response
     * @throws ApiException if application, interviewer, or position not found
     */
    @Transactional
    public RecruitmentResponse scheduleInterview(CreateRecruitmentRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        ApplicationId appId = new ApplicationId(
                request.applicantId(), request.positionId(), request.applicationDate());
        Application application = applicationRepository.findById(appId)
                .orElseThrow(() -> new ApiException(HrErrorCode.APPLICATION_NOT_FOUND));
        Person interviewer = findPersonOrThrow(request.interviewerId());

        Recruitment recruitment = new Recruitment();
        recruitment.setInterviewer(interviewer);
        recruitment.setApplicant(application.getApplicant());
        recruitment.setPosition(application.getPosition());
        recruitment.setApplicationDate(application.getApplicationDate());
        recruitment.setInterviewDate(request.interviewDate());
        recruitment.setInterviewResult("Pending");
        recruitment = recruitmentRepository.save(recruitment);
        log.info("Interview scheduled: applicant={}, interviewer={}, date={}",
                request.applicantId(), request.interviewerId(), request.interviewDate());
        return toRecruitmentResponse(recruitment);
    }

    /**
     * Records the result of an interview.
     *
     * @param id      the 5-part composite key identifying the interview
     * @param request the interview result (Pass, Fail, Pending)
     * @return the updated recruitment response
     * @throws ApiException if the interview record is not found or result is invalid
     */
    @Transactional
    public RecruitmentResponse recordResult(RecruitmentId id, UpdateRecruitmentRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HrErrorCode.RECRUITMENT_NOT_FOUND));

        String result = request.interviewResult();
        if (!"Pass".equals(result) && !"Fail".equals(result) && !"Pending".equals(result)) {
            throw new ApiException(HrErrorCode.INTERVIEW_RESULT_INVALID);
        }

        recruitment.setInterviewResult(result);
        recruitment = recruitmentRepository.save(recruitment);
        log.info("Interview result recorded: applicant={}, result={}",
                id.getApplicant(), result);
        return toRecruitmentResponse(recruitment);
    }

    // ── Hire Agreement (Transactional Onboarding) ───────────────────────

    /**
     * Creates a hire agreement and onboards the applicant.
     *
     * <p>In a single transaction (US-1 step 7):
     * <ol>
     *   <li>Validates at least one interview has result = 'Pass'</li>
     *   <li>Validates employment start date >= application date</li>
     *   <li>Creates the HireAgreement record</li>
     *   <li>Creates StaffProfile for the applicant</li>
     *   <li>Creates initial StaffPositionHistory (authorized_by = null for hire)</li>
     *   <li>Creates initial StaffSalaryHistory (approved_by = null for hire)</li>
     * </ol>
     *
     * @param request the hire agreement details
     * @return the created hire agreement response
     * @throws ApiException if application, person, position, or qualification not found, or rules fail
     */
    @Transactional
    public HireAgreementResponse createHireAgreement(CreateHireAgreementRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        ApplicationId appId = new ApplicationId(
                request.applicantId(), request.positionId(), request.applicationDate());
        Application application = applicationRepository.findById(appId)
                .orElseThrow(() -> new ApiException(HrErrorCode.APPLICATION_NOT_FOUND));

        // Validate at least one Pass
        List<Recruitment> interviews = recruitmentRepository
                .findByApplicantIdAndPositionIdAndApplicationDate(
                        request.applicantId(), request.positionId(), request.applicationDate());
        hireAgreementRules.validateAtLeastOnePass(interviews);

        // Validate start date
        hireAgreementRules.validateStartDateNotBeforeApplication(
                request.employmentStartDate(), request.applicationDate());

        // 1. Create HireAgreement
        HireAgreement agreement = new HireAgreement();
        agreement.setApplicant(application.getApplicant());
        agreement.setPosition(application.getPosition());
        agreement.setApplicationDate(application.getApplicationDate());
        agreement.setEmploymentStartDate(request.employmentStartDate());
        agreement.setOfferedBaseDailyRate(request.offeredBaseDailyRate());
        agreement.setOfferedMaximumSalary(request.offeredMaximumSalary());
        agreement = hireAgreementRepository.save(agreement);

        Person applicant = application.getApplicant();
        StaffPosition position = application.getPosition();

        // 2. Create StaffProfile (if not already exists)
        if (!staffProfileRepository.existsById(applicant.getId())) {
            Qualification qualification = qualificationRepository.findById(request.qualificationId())
                    .orElseThrow(() -> new ApiException(HrErrorCode.QUALIFICATION_NOT_FOUND));

            StaffProfile profile = StaffProfile.builder()
                    .person(applicant)
                    .qualification(qualification)
                    .build();
            staffProfileRepository.save(profile);
        }

        // 3. Create initial StaffPositionHistory (authorized_by = null for hire)
        StaffPositionHistory positionHistory = new StaffPositionHistory();
        positionHistory.setPerson(applicant);
        positionHistory.setPosition(position);
        positionHistory.setEffectiveDate(request.employmentStartDate());
        positionHistory.setAuthorizedBy(null); // null for initial hire
        staffPositionHistoryRepository.save(positionHistory);

        // 4. Create initial StaffSalaryHistory (approved_by = null for hire)
        StaffSalaryHistory salaryHistory = new StaffSalaryHistory();
        salaryHistory.setStaff(applicant);
        salaryHistory.setEffectiveDate(request.employmentStartDate());
        salaryHistory.setBaseDailyRate(request.offeredBaseDailyRate());
        salaryHistory.setMaximumSalary(request.offeredMaximumSalary() != null
                ? request.offeredMaximumSalary()
                : request.offeredBaseDailyRate().multiply(java.math.BigDecimal.valueOf(30)));
        salaryHistory.setApprovedBy(null); // null for initial hire
        staffSalaryHistoryRepository.save(salaryHistory);

        log.info("Hire agreement created and applicant onboarded: applicant={}, position={}",
                request.applicantId(), request.positionId());
        return toHireAgreementResponse(agreement);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Person findPersonOrThrow(UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ApiException(HrErrorCode.PERSON_NOT_FOUND));
    }

    private StaffPosition findPositionOrThrow(UUID id) {
        return staffPositionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));
    }

    private ApplicationResponse toApplicationResponse(Application a) {
        return new ApplicationResponse(
                a.getApplicant().getId(),
                a.getPosition().getId(),
                a.getApplicationDate());
    }

    private RecruitmentResponse toRecruitmentResponse(Recruitment r) {
        return new RecruitmentResponse(
                r.getInterviewer().getId(),
                r.getApplicant().getId(),
                r.getPosition().getId(),
                r.getApplicationDate(),
                r.getInterviewDate(),
                r.getInterviewResult());
    }

    private HireAgreementResponse toHireAgreementResponse(HireAgreement h) {
        return new HireAgreementResponse(
                h.getApplicant().getId(),
                h.getPosition().getId(),
                h.getApplicationDate(),
                h.getEmploymentStartDate(),
                h.getOfferedBaseDailyRate(),
                h.getOfferedMaximumSalary());
    }
}
