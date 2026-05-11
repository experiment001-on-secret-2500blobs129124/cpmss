package com.cpmss.hr.application;

import com.cpmss.hr.application.dto.ApplicationCvResponse;
import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;
import com.cpmss.platform.storage.ObjectStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

/**
 * Orchestrates current CV upload/download behavior for application records.
 *
 * <p>The application row stores only metadata and a MinIO object key. Binary
 * file bytes stay in object storage. Re-upload replaces the current metadata
 * reference for the same application while keeping the operation scoped to the
 * owning applicant or HR administrators.
 */
@Service
public class ApplicationCvService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationCvService.class);
    private static final Duration DOWNLOAD_URL_EXPIRY = Duration.ofMinutes(10);

    private final ApplicationRepository applicationRepository;
    private final PersonRepository personRepository;
    private final CurrentUserService currentUserService;
    private final ObjectStorageService objectStorageService;
    private final HrAccessRules accessRules = new HrAccessRules();
    private final ApplicationCvRules rules = new ApplicationCvRules();

    /**
     * Constructs the service with application metadata and object storage access.
     *
     * @param applicationRepository application repository
     * @param personRepository      person repository for uploader lookup
     * @param currentUserService    current-user resolver
     * @param objectStorageService  object storage boundary
     */
    public ApplicationCvService(ApplicationRepository applicationRepository,
                                PersonRepository personRepository,
                                CurrentUserService currentUserService,
                                ObjectStorageService objectStorageService) {
        this.applicationRepository = applicationRepository;
        this.personRepository = personRepository;
        this.currentUserService = currentUserService;
        this.objectStorageService = objectStorageService;
    }

    /**
     * Uploads or replaces the current CV reference for an application.
     *
     * @param applicantId     application applicant UUID
     * @param positionId      application position UUID
     * @param applicationDate application date
     * @param file            uploaded CV file
     * @return current CV metadata after the update
     */
    @Transactional
    public ApplicationCvResponse uploadCurrentCv(UUID applicantId, UUID positionId,
                                                 LocalDate applicationDate,
                                                 MultipartFile file) {
        CurrentUser user = currentUserService.currentUser();
        accessRules.requireCanUploadApplicationCv(user, applicantId);
        UUID uploaderId = user.requirePersonId("Application CV upload");

        if (file == null || file.isEmpty()) {
            throw new ApiException(HrErrorCode.APPLICATION_CV_FILE_REQUIRED);
        }
        rules.validateUploadMetadata(file.getOriginalFilename(), file.getContentType(), file.getSize());

        Application application = findApplication(applicantId, positionId, applicationDate);
        Person uploader = personRepository.findById(uploaderId)
                .orElseThrow(() -> new ApiException(HrErrorCode.PERSON_NOT_FOUND));
        String previousObjectKey = application.getCvObjectKey();
        String objectKey = buildObjectKey(applicantId, positionId, applicationDate,
                file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            objectStorageService.putObject(objectKey, inputStream, file.getSize(), file.getContentType());
        } catch (IOException ex) {
            throw new ApiException(CommonErrorCode.FILE_STORAGE_FAILURE);
        }

        try {
            application.setCvObjectKey(objectKey);
            application.setCvOriginalFilename(file.getOriginalFilename());
            application.setCvContentType(file.getContentType());
            application.setCvSizeBytes(file.getSize());
            application.setCvUploadedAt(Instant.now());
            application.setCvUploadedBy(uploader);
            Application saved = applicationRepository.save(application);
            if (previousObjectKey != null && !previousObjectKey.equals(objectKey)) {
                deletePreviousObjectBestEffort(previousObjectKey);
            }
            return toResponse(saved, null);
        } catch (RuntimeException ex) {
            deleteNewObjectAfterMetadataFailure(objectKey, ex);
            throw ex;
        }
    }

    /**
     * Creates a temporary download URL for the current application CV.
     *
     * @param applicantId     application applicant UUID
     * @param positionId      application position UUID
     * @param applicationDate application date
     * @return current CV metadata with a download URL
     */
    @Transactional(readOnly = true)
    public ApplicationCvResponse createCurrentCvDownloadUrl(UUID applicantId,
                                                            UUID positionId,
                                                            LocalDate applicationDate) {
        CurrentUser user = currentUserService.currentUser();
        accessRules.requireCanViewApplication(user, applicantId);
        Application application = findApplication(applicantId, positionId, applicationDate);
        rules.requireCurrentCv(application);
        String url = objectStorageService.createDownloadUrl(
                application.getCvObjectKey(),
                safeFilename(application.getCvOriginalFilename()),
                DOWNLOAD_URL_EXPIRY);
        return toResponse(application, url);
    }

    private void deletePreviousObjectBestEffort(String previousObjectKey) {
        try {
            objectStorageService.deleteObject(previousObjectKey);
        } catch (RuntimeException cleanupFailure) {
            log.warn("Previous application CV object cleanup failed after metadata replacement");
        }
    }

    private void deleteNewObjectAfterMetadataFailure(String objectKey, RuntimeException original) {
        try {
            objectStorageService.deleteObject(objectKey);
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }

    private Application findApplication(UUID applicantId, UUID positionId,
                                        LocalDate applicationDate) {
        return applicationRepository.findById(new ApplicationId(applicantId, positionId, applicationDate))
                .orElseThrow(() -> new ApiException(HrErrorCode.APPLICATION_NOT_FOUND));
    }

    private ApplicationCvResponse toResponse(Application application, String downloadUrl) {
        UUID uploadedById = application.getCvUploadedBy() != null
                ? application.getCvUploadedBy().getId()
                : null;
        return new ApplicationCvResponse(
                application.getApplicant().getId(),
                application.getPosition().getId(),
                application.getApplicationDate(),
                application.getCvOriginalFilename(),
                application.getCvContentType(),
                application.getCvSizeBytes(),
                application.getCvUploadedAt(),
                uploadedById,
                downloadUrl);
    }

    private String buildObjectKey(UUID applicantId, UUID positionId,
                                  LocalDate applicationDate, String originalFilename) {
        return "applications/%s/%s/%s/%s/%s".formatted(
                applicantId,
                positionId,
                applicationDate,
                UUID.randomUUID(),
                safeFilename(originalFilename));
    }

    private String safeFilename(String originalFilename) {
        String trimmed = originalFilename == null || originalFilename.isBlank()
                ? "cv"
                : originalFilename.trim();
        String withoutPath = trimmed.replace('\\', '/');
        int slash = withoutPath.lastIndexOf('/');
        String filename = slash >= 0 ? withoutPath.substring(slash + 1) : withoutPath;
        String safe = filename.replaceAll("[^A-Za-z0-9._-]", "_")
                .replaceAll("_+", "_");
        return safe.toLowerCase(Locale.ROOT).isBlank() ? "cv" : safe;
    }
}
