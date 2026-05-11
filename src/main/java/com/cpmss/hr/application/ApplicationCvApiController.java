package com.cpmss.hr.application;

import com.cpmss.hr.application.dto.ApplicationCvResponse;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST controller for current CV actions on application records.
 */
@RestController
public class ApplicationCvApiController {

    private final ApplicationCvService applicationCvService;

    /**
     * Constructs the controller.
     *
     * @param applicationCvService current application CV service
     */
    public ApplicationCvApiController(ApplicationCvService applicationCvService) {
        this.applicationCvService = applicationCvService;
    }

    /**
     * Uploads or replaces the current CV for an application.
     *
     * @param applicantId     application applicant UUID
     * @param positionId      application position UUID
     * @param applicationDate application date
     * @param file            CV file content
     * @return current CV metadata
     */
    @PutMapping(value = ApiPaths.APPLICATIONS_CV, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationCvResponse>> uploadCurrentCv(
            @RequestParam UUID applicantId,
            @RequestParam UUID positionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate applicationDate,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok(applicationCvService.uploadCurrentCv(
                applicantId, positionId, applicationDate, file)));
    }

    /**
     * Creates a short-lived URL for downloading the current application CV.
     *
     * @param applicantId     application applicant UUID
     * @param positionId      application position UUID
     * @param applicationDate application date
     * @return current CV metadata and download URL
     */
    @GetMapping(ApiPaths.APPLICATIONS_CV_DOWNLOAD_URL)
    public ResponseEntity<ApiResponse<ApplicationCvResponse>> createCurrentCvDownloadUrl(
            @RequestParam UUID applicantId,
            @RequestParam UUID positionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate applicationDate) {
        return ResponseEntity.ok(ApiResponse.ok(applicationCvService.createCurrentCvDownloadUrl(
                applicantId, positionId, applicationDate)));
    }
}
