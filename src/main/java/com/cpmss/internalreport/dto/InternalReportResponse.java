package com.cpmss.internalreport.dto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response payload for an internal report.
 */
public record InternalReportResponse(
        UUID id,
        UUID reporterId,
        String assignedToRole,
        String subject,
        String body,
        String reportCategory,
        String priority,
        String reportStatus,
        boolean isRead,
        OffsetDateTime readAt,
        UUID readById,
        UUID resolvedById,
        OffsetDateTime resolvedAt,
        String resolutionNote,
        Instant createdAt,
        Instant updatedAt
) {}
