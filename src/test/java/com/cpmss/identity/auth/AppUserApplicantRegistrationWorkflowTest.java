package com.cpmss.identity.auth;

import com.cpmss.hr.application.Application;
import com.cpmss.hr.application.ApplicationRepository;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.hr.staffposition.StaffPositionRepository;
import com.cpmss.identity.auth.dto.AppUserResponse;
import com.cpmss.identity.auth.dto.ApplicantRegistrationResponse;
import com.cpmss.identity.auth.dto.RegisterApplicantRequest;
import com.cpmss.people.common.PassportNumber;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Protects the public applicant registration workflow.
 */
@ExtendWith(MockitoExtension.class)
class AppUserApplicantRegistrationWorkflowTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private StaffPositionRepository staffPositionRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private AppUserMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    /**
     * Duplicate passports must stop the public workflow before account or
     * application rows are written.
     */
    @Test
    void registerApplicantRejectsDuplicatePassportBeforeWritingWorkflowRows() {
        RegisterApplicantRequest request = applicantRequest(UUID.randomUUID());
        PassportNumber passportNo = PassportNumber.of(request.passportNo());
        when(personRepository.existsByPassportNo(passportNo)).thenReturn(true);

        AppUserService service = service();

        assertThatThrownBy(() -> service.registerApplicant(request))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(PeopleErrorCode.PASSPORT_DUPLICATE));

        verify(staffPositionRepository, never()).findById(any(UUID.class));
        verify(repository, never()).save(any(AppUser.class));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    /**
     * Successful registration writes Person, AppUser, and Application rows in
     * one orchestration path and links the returned identifiers.
     */
    @Test
    void registerApplicantCreatesProfileAccountAndFirstApplication() {
        UUID positionId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RegisterApplicantRequest request = applicantRequest(positionId);
        StaffPosition position = new StaffPosition();
        position.setId(positionId);

        when(staffPositionRepository.findById(positionId)).thenReturn(Optional.of(position));
        when(passwordEncoder.encode(request.password())).thenReturn("hash");
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            Person person = invocation.getArgument(0);
            person.setId(personId);
            return person;
        });
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });
        when(mapper.toResponse(any(AppUser.class))).thenReturn(new AppUserResponse(
                userId, request.email(), SystemRole.APPLICANT, true, personId, false, Instant.now()));

        AppUserService service = service();
        ApplicantRegistrationResponse response = service.registerApplicant(request);

        assertThat(response.user().id()).isEqualTo(userId);
        assertThat(response.personId()).isEqualTo(personId);
        assertThat(response.positionId()).isEqualTo(positionId);
        assertThat(response.applicationDate()).isEqualTo(request.applicationDate());
        verify(applicationRepository).save(argThat(application ->
                application.getApplicant().getId().equals(personId)
                        && application.getPosition().getId().equals(positionId)
                        && application.getApplicationDate().equals(request.applicationDate())));
    }

    private AppUserService service() {
        return new AppUserService(
                repository,
                passwordEncoder,
                personRepository,
                staffPositionRepository,
                applicationRepository,
                mapper,
                currentUserService);
    }

    private RegisterApplicantRequest applicantRequest(UUID positionId) {
        return new RegisterApplicantRequest(
                "applicant@example.com",
                "password123",
                "P1234567",
                "Nora",
                "Applicant",
                "+20",
                "1002003000",
                positionId,
                LocalDate.of(2026, 5, 10));
    }
}
