package com.cpmss.hr.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for current application CV metadata and download access.
 *
 * @param applicantId        the applicant's person UUID
 * @param positionId         the position UUID
 * @param applicationDate    the application date
 * @param originalFilename   display filename for the current CV
 * @param contentType        MIME type of the current CV
 * @param sizeBytes          uploaded object size in bytes
 * @param uploadedAt         upload timestamp
 * @param uploadedById       person UUID that uploaded or replaced the CV
 * @param downloadUrl        short-lived download URL, when requested
 */
public record ApplicationCvResponse(
        UUID applicantId,
        UUID positionId,
        LocalDate applicationDate,
        String originalFilename,
        String contentType,
        Long sizeBytes,
        Instant uploadedAt,
        UUID uploadedById,
        String downloadUrl
) {}
