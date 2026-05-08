package com.cpmss.hr.staffposition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a staff position.
 *
 * @param positionName the position title
 * @param departmentId the owning department's UUID
 */
public record CreateStaffPositionRequest(
        @NotBlank @Size(max = 100) String positionName,
        @NotNull UUID departmentId
) {}
