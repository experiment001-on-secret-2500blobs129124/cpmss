package com.cpmss.internalreport.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for marking a report as read.
 *
 * @param readById the person marking as read
 */
public record MarkReportReadRequest(
        @NotNull UUID readById
) {}
