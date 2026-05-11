package com.cpmss.hr.recruitment;

import com.cpmss.hr.application.Application;
import com.cpmss.hr.application.ApplicationRepository;
import com.cpmss.hr.hireagreement.HireAgreementRepository;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.hr.staffposition.StaffPositionRepository;
import com.cpmss.hr.staffposition.PositionSalaryHistoryRepository;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffprofile.StaffProfileRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.people.qualification.QualificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Verifies applicant-scoped visibility in the recruitment workflow.
 */
@ExtendWith(MockitoExtension.class)
class RecruitmentVisibilityWorkflowTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private HireAgreementRepository hireAgreementRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private StaffPositionRepository staffPositionRepository;

    @Mock
    private StaffProfileRepository staffProfileRepository;

    @Mock
    private StaffPositionHistoryRepository staffPositionHistoryRepository;

    @Mock
    private StaffSalaryHistoryRepository staffSalaryHistoryRepository;

    @Mock
    private PositionSalaryHistoryRepository positionSalaryHistoryRepository;

    @Mock
    private QualificationRepository qualificationRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void applicantCanListOnlyOwnApplications() {
        UUID applicantId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        LocalDate applicationDate = LocalDate.of(2026, 5, 10);
        Application application = application(applicantId, positionId, applicationDate);
        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), applicantId, SystemRole.APPLICANT, "applicant@example.com"));
        when(applicationRepository.findByApplicantIdOrderByApplicationDateDesc(applicantId)).thenReturn(List.of(application));

        var response = service().listMyApplications();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().applicantId()).isEqualTo(applicantId);
        assertThat(response.getFirst().positionId()).isEqualTo(positionId);
        assertThat(response.getFirst().applicationDate()).isEqualTo(applicationDate);
    }

    @Test
    void applicantCanListOnlyOwnInterviews() {
        UUID applicantId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        UUID interviewerId = UUID.randomUUID();
        LocalDate applicationDate = LocalDate.of(2026, 5, 10);
        Recruitment interview = interview(interviewerId, applicantId, positionId, applicationDate);
        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), applicantId, SystemRole.APPLICANT, "applicant@example.com"));
        when(recruitmentRepository.findByApplicantIdOrderByInterviewDateDesc(applicantId)).thenReturn(List.of(interview));

        var response = service().listMyInterviews();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().applicantId()).isEqualTo(applicantId);
        assertThat(response.getFirst().interviewerId()).isEqualTo(interviewerId);
        assertThat(response.getFirst().interviewResult()).isEqualTo("Pending");
    }

    private RecruitmentService service() {
        return new RecruitmentService(
                applicationRepository,
                recruitmentRepository,
                hireAgreementRepository,
                personRepository,
                staffPositionRepository,
                staffProfileRepository,
                staffPositionHistoryRepository,
                staffSalaryHistoryRepository,
                positionSalaryHistoryRepository,
                qualificationRepository,
                currentUserService);
    }

    private Application application(UUID applicantId, UUID positionId, LocalDate date) {
        Application application = new Application();
        application.setApplicant(person(applicantId));
        StaffPosition position = new StaffPosition();
        position.setId(positionId);
        application.setPosition(position);
        application.setApplicationDate(date);
        return application;
    }

    private Recruitment interview(UUID interviewerId, UUID applicantId,
                                  UUID positionId, LocalDate applicationDate) {
        Recruitment recruitment = new Recruitment();
        recruitment.setInterviewer(person(interviewerId));
        recruitment.setApplicant(person(applicantId));
        StaffPosition position = new StaffPosition();
        position.setId(positionId);
        recruitment.setPosition(position);
        recruitment.setApplicationDate(applicationDate);
        recruitment.setInterviewDate(applicationDate.plusDays(3));
        recruitment.setInterviewResult("Pending");
        return recruitment;
    }

    private Person person(UUID id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }
}
