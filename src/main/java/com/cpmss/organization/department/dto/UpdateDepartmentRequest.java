package com.cpmss.organization.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing department.
 *
 * @param departmentName the updated department name
 */
public record UpdateDepartmentRequest(
        @NotBlank @Size(max = 100) String departmentName
) {}
