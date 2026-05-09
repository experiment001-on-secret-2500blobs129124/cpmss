package com.cpmss.communication.internalreport.dto;

import com.cpmss.communication.internalreport.ReportCategory;
import com.cpmss.communication.internalreport.ReportPriority;
import com.cpmss.identity.auth.SystemRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for filing an internal report.
 *
 * @param reporterId     the person filing the report
 * @param assignedToRole the system role group to see this report
 * @param subject        short subject line
 * @param body           full report body
 * @param reportCategory category (Salary_Request, Complaint, etc.)
 * @param priority       priority level (Low, Normal, High, Urgent)
 */
public record CreateInternalReportRequest(
        @NotNull UUID reporterId,
        @NotNull SystemRole assignedToRole,
        @NotBlank @Size(max = 200) String subject,
        @NotBlank String body,
        @NotNull ReportCategory reportCategory,
        ReportPriority priority
) {}
