package com.cpmss.communication.internalreport.dto;

import com.cpmss.communication.internalreport.ReportStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for updating an internal report (status changes, resolution).
 *
 * @param reportStatus   new status (Open, In_Review, Resolved, Rejected)
 * @param resolutionNote resolution note / explanation (optional)
 * @param resolvedById   the person resolving the report (optional)
 */
public record UpdateInternalReportRequest(
        @NotNull ReportStatus reportStatus,
        String resolutionNote,
        UUID resolvedById
) {}
