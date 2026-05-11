package com.cpmss.hr.application;

import com.cpmss.hr.application.dto.ApplicationCvResponse;
import com.cpmss.platform.common.ApiPaths;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies HTTP binding for current application CV endpoints.
 */
class ApplicationCvApiControllerTest {

    private final ApplicationCvService applicationCvService = mock(ApplicationCvService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ApplicationCvApiController(applicationCvService))
            .build();

    @Test
    void multipartUploadBindsCompositeApplicationKeyAndFile() throws Exception {
        UUID applicantId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        LocalDate applicationDate = LocalDate.of(2026, 5, 10);
        MockMultipartFile file = new MockMultipartFile(
                "file", "cv.pdf", "application/pdf", "content".getBytes());
        var response = response(applicantId, positionId, applicationDate, null);
        when(applicationCvService.uploadCurrentCv(eq(applicantId), eq(positionId),
                eq(applicationDate), any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart(ApiPaths.APPLICATIONS_CV)
                        .file(file)
                        .param("applicantId", applicantId.toString())
                        .param("positionId", positionId.toString())
                        .param("applicationDate", applicationDate.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.originalFilename").value("cv.pdf"));

        verify(applicationCvService).uploadCurrentCv(eq(applicantId), eq(positionId),
                eq(applicationDate), any(MultipartFile.class));
    }

    @Test
    void downloadUrlEndpointBindsCompositeApplicationKey() throws Exception {
        UUID applicantId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        LocalDate applicationDate = LocalDate.of(2026, 5, 10);
        var response = response(applicantId, positionId, applicationDate, "http://minio/download");
        when(applicationCvService.createCurrentCvDownloadUrl(applicantId, positionId, applicationDate))
                .thenReturn(response);

        mockMvc.perform(get(ApiPaths.APPLICATIONS_CV_DOWNLOAD_URL)
                        .param("applicantId", applicantId.toString())
                        .param("positionId", positionId.toString())
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.downloadUrl").value("http://minio/download"));

        verify(applicationCvService).createCurrentCvDownloadUrl(applicantId, positionId, applicationDate);
    }

    private ApplicationCvResponse response(UUID applicantId, UUID positionId,
                                           LocalDate applicationDate, String downloadUrl) {
        return new ApplicationCvResponse(
                applicantId,
                positionId,
                applicationDate,
                "cv.pdf",
                "application/pdf",
                7L,
                Instant.parse("2026-05-10T12:00:00Z"),
                applicantId,
                downloadUrl);
    }
}
