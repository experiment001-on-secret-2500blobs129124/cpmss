package com.cpmss.communication.internalreport.dto;

import com.cpmss.communication.internalreport.ReportCategory;
import com.cpmss.communication.internalreport.ReportPriority;
import com.cpmss.communication.internalreport.ReportStatus;
import com.cpmss.identity.auth.SystemRole;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response payload for an internal report.
 */
public record InternalReportResponse(
        UUID id,
        UUID reporterId,
        SystemRole assignedToRole,
        String subject,
        String body,
        ReportCategory reportCategory,
        ReportPriority priority,
        ReportStatus reportStatus,
        boolean isRead,
        OffsetDateTime readAt,
        UUID readById,
        UUID resolvedById,
        OffsetDateTime resolvedAt,
        String resolutionNote,
        Instant createdAt,
        Instant updatedAt
) {}
