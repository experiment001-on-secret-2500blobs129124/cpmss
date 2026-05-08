package com.cpmss.communication.internalreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an internal report (status changes, resolution).
 *
 * @param reportStatus   new status (Open, In_Review, Resolved, Rejected)
 * @param resolutionNote resolution note / explanation (optional)
 * @param resolvedById   the person resolving the report (optional)
 */
public record UpdateInternalReportRequest(
        @NotBlank @Size(max = 20) String reportStatus,
        String resolutionNote,
        UUID resolvedById
) {}
