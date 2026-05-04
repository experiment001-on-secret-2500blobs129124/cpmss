package com.cpmss.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new department.
 *
 * @param departmentName the department's name
 */
public record CreateDepartmentRequest(
        @NotBlank @Size(max = 100) String departmentName
) {}
