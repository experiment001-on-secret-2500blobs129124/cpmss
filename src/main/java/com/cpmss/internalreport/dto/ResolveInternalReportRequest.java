package com.cpmss.internalreport.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for resolving an internal report.
 *
 * @param resolvedById   the person resolving the report
 * @param resolutionNote the resolution explanation (optional)
 */
public record ResolveInternalReportRequest(
        @NotNull UUID resolvedById,
        String resolutionNote
) {}
