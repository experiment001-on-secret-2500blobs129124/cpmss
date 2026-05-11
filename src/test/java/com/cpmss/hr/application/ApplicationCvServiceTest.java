package com.cpmss.hr.application;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.storage.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies current CV metadata workflow on application records.
 */
@ExtendWith(MockitoExtension.class)
class ApplicationCvServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ObjectStorageService objectStorageService;

    @Test
    void applicantCanReplaceOwnCurrentCvAndOldObjectIsRemovedAfterSave() {
        UUID applicantId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        LocalDate applicationDate = LocalDate.of(2026, 5, 10);
        Application application = application(applicantId, positionId, applicationDate);
        application.setCvObjectKey("applications/old/cv.pdf");
        Person uploader = person(applicantId);
        MockMultipartFile file = new MockMultipartFile(
                "file", "My CV.pdf", "application/pdf", "content".getBytes());

        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), applicantId, SystemRole.APPLICANT, "applicant@example.com"));
        when(applicationRepository.findById(new ApplicationId(applicantId, positionId, applicationDate)))
                .thenReturn(Optional.of(application));
        when(personRepository.findById(applicantId)).thenReturn(Optional.of(uploader));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service().uploadCurrentCv(applicantId, positionId, applicationDate, file);

        assertThat(response.originalFilename()).isEqualTo("My CV.pdf");
        assertThat(response.contentType()).isEqualTo("application/pdf");
        assertThat(response.sizeBytes()).isEqualTo(file.getSize());
        assertThat(response.uploadedById()).isEqualTo(applicantId);
        assertThat(response.downloadUrl()).isNull();
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(objectStorageService).putObject(keyCaptor.capture(), any(InputStream.class),
                eq(file.getSize()), eq("application/pdf"));
        assertThat(keyCaptor.getValue()).startsWith("applications/%s/%s/%s/"
                .formatted(applicantId, positionId, applicationDate));
        assertThat(application.getCvObjectKey()).isEqualTo(keyCaptor.getValue());
        verify(objectStorageService).deleteObject("applications/old/cv.pdf");
    }

    @Test
    void applicantCannotUploadCvForAnotherApplicantApplication() {
        UUID applicantId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), UUID.randomUUID(), SystemRole.APPLICANT, "other@example.com"));
        MockMultipartFile file = new MockMultipartFile(
                "file", "cv.pdf", "application/pdf", "content".getBytes());

        assertThatThrownBy(() -> service().uploadCurrentCv(
                applicantId, UUID.randomUUID(), LocalDate.now(), file))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(HrErrorCode.HR_RECORD_ACCESS_DENIED));

        verify(applicationRepository, never()).findById(any(ApplicationId.class));
        verify(objectStorageService, never()).putObject(any(), any(), eq(file.getSize()), any());
    }

    @Test
    void downloadUrlRequiresExistingCurrentCvMetadata() {
        UUID applicantId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        LocalDate applicationDate = LocalDate.of(2026, 5, 10);
        Application application = application(applicantId, positionId, applicationDate);
        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), applicantId, SystemRole.APPLICANT, "applicant@example.com"));
        when(applicationRepository.findById(new ApplicationId(applicantId, positionId, applicationDate)))
                .thenReturn(Optional.of(application));

        assertThatThrownBy(() -> service().createCurrentCvDownloadUrl(
                applicantId, positionId, applicationDate))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(HrErrorCode.APPLICATION_CV_NOT_FOUND));

        verify(objectStorageService, never()).createDownloadUrl(any(), any(), any());
    }

    private ApplicationCvService service() {
        return new ApplicationCvService(
                applicationRepository,
                personRepository,
                currentUserService,
                objectStorageService);
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

    private Person person(UUID id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }
}
