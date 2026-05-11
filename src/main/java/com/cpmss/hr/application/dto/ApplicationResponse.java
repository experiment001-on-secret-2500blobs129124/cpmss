package com.cpmss.hr.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a job application.
 *
 * @param applicantId        the applicant's person UUID
 * @param positionId         the position UUID
 * @param applicationDate    the date of the application
 * @param cvOriginalFilename current CV display filename, if uploaded
 * @param cvContentType      current CV MIME type, if uploaded
 * @param cvSizeBytes        current CV size in bytes, if uploaded
 * @param cvUploadedAt       current CV upload timestamp, if uploaded
 * @param cvUploadedById     person UUID that uploaded the current CV, if uploaded
 */
public record ApplicationResponse(
        UUID applicantId,
        UUID positionId,
        LocalDate applicationDate,
        String cvOriginalFilename,
        String cvContentType,
        Long cvSizeBytes,
        Instant cvUploadedAt,
        UUID cvUploadedById
) {}
